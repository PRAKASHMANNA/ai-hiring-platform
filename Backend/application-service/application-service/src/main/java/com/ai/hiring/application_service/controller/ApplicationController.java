package com.ai.hiring.application_service.controller;

import com.ai.hiring.application_service.dto.ApplicationRequest;
import com.ai.hiring.application_service.dto.ApplicationResponse;
import com.ai.hiring.application_service.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;

    // Candidate — job pe apply karo
    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/apply")
    public ResponseEntity<ApplicationResponse> applyForJob(
            @Valid @RequestBody ApplicationRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(
                applicationService.applyForJob(request, email, email));
    }

    // Candidate — apne applications dekho
    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/my-applications")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(
                applicationService.getMyApplications(email));
    }

    // Recruiter — job ke applications dekho
    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByJob(
            @PathVariable Long jobId) {
        return ResponseEntity.ok(
                applicationService.getApplicationsByJob(jobId));
    }

    // Recruiter — status update karo
    @PreAuthorize("hasRole('CANDIDATE')")
    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(
                applicationService.updateStatus(id, status, note));
    }
}