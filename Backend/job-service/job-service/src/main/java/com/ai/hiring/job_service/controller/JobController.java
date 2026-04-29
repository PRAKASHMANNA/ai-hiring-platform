package com.ai.hiring.job_service.controller;

import com.ai.hiring.job_service.dto.JobRequest;
import com.ai.hiring.job_service.dto.JobResponse;
import com.ai.hiring.job_service.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobController {

    private final JobService jobService;

    // Recruiter — job post
    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody JobRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(jobService.createJob(request, email));
    }

    // Public — see the active jobs
    @GetMapping("/public/all")
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllActiveJobs());
    }

    // Public — see the single job
    @GetMapping("/public/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    // Recruiter — your Jobs (apne jobs dekho)
    @GetMapping("/my-jobs")
    public ResponseEntity<List<JobResponse>> getMyJobs(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(jobService.getJobsByRecruiter(email));
    }

    // Recruiter — job update
    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(jobService.updateJob(id, request, email));
    }

    // Recruiter — job delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        jobService.deleteJob(id, email);
        return ResponseEntity.ok("Job deleted successfully");
    }

    // Search jobs
    @GetMapping("/public/search")
    public ResponseEntity<List<JobResponse>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location) {
        return ResponseEntity.ok(jobService.searchJobs(title, location));
    }
}
