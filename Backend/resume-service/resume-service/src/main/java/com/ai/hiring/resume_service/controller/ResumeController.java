package com.ai.hiring.resume_service.controller;


import com.ai.hiring.resume_service.dto.AnalyzeRequest;
import com.ai.hiring.resume_service.dto.ResumeResponse;
import com.ai.hiring.resume_service.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResumeController {

    private final ResumeService resumeService;

    // Resume upload
    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/upload")
    public ResponseEntity<ResumeResponse> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("candidateName") String candidateName,
            @AuthenticationPrincipal String email) throws IOException {
        return ResponseEntity.ok(
                resumeService.uploadResume(file, email, candidateName));
    }

    // AI Analyze
    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/analyze")
    public ResponseEntity<ResumeResponse> analyzeResume(
            @RequestBody AnalyzeRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(
                resumeService.analyzeResume(request, email));
    }

    // Apne saare resumes dekho
    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/my-resumes")
    public ResponseEntity<List<ResumeResponse>> getMyResumes(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(resumeService.getMyResumes(email));
    }

    // Single resume by ID

    @GetMapping("/{id}")
    public ResponseEntity<ResumeResponse> getResumeById(
            @PathVariable String id) {
        return ResponseEntity.ok(resumeService.getResumeById(id));
    }
}