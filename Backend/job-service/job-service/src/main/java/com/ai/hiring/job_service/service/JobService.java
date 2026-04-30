package com.ai.hiring.job_service.service;


import com.ai.hiring.job_service.dto.JobRequest;
import com.ai.hiring.job_service.dto.JobResponse;
import com.ai.hiring.job_service.model.Job;
import com.ai.hiring.job_service.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final JobRepository jobRepository;

    // Create the Jobs(Job create karo) (Recruiter)
    public JobResponse createJob(JobRequest request, String recruiterEmail) {
        Job job = Job.builder()
                .title(request.getTitle())
                .company(request.getCompany())
                .location(request.getLocation())
                .description(request.getDescription())
                .requirements(request.getRequirements())
                .salary(request.getSalary())
                .jobType(request.getJobType())
                .experienceLevel(request.getExperienceLevel())
                .postedBy(recruiterEmail)
                .build();

        Job saved = jobRepository.save(job);
        // Recruiter ko email bhejo - mail the recruiter
        kafkaTemplate.send("job-alerts", recruiterEmail,
                recruiterEmail + "::" +
                        "Your job '" + request.getTitle() + "' at " + request.getCompany() +
                        " has been posted successfully! Location: " + request.getLocation() +
                        ". Salary: " + request.getSalary());
        return mapToResponse(saved);
    }

    // all active jobs (Candidate)
    /*public List<JobResponse> getAllActiveJobs() {
        return jobRepository.findByStatus(Job.JobStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }*/
    public Page<JobResponse> getAllActiveJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jobRepository.findByStatus(Job.JobStatus.ACTIVE, pageable)
                .map(this::mapToResponse);
    }

    // Single job by ID
    public JobResponse getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return mapToResponse(job);
    }

    // Recruiter ke all(saare) jobs
    public List<JobResponse> getJobsByRecruiter(String email) {
        return jobRepository.findByPostedBy(email)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Job update
    public JobResponse updateJob(Long id, JobRequest request, String recruiterEmail) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedBy().equals(recruiterEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setDescription(request.getDescription());
        job.setRequirements(request.getRequirements());
        job.setSalary(request.getSalary());
        job.setJobType(request.getJobType());
        job.setExperienceLevel(request.getExperienceLevel());

        Job updated = jobRepository.save(job);
        return mapToResponse(updated);
    }

    // Job delete
    public void deleteJob(Long id, String recruiterEmail) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getPostedBy().equals(recruiterEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        jobRepository.delete(job);
    }

    // Search jobs
    public List<JobResponse> searchJobs(String title, String location) {
        if (title != null && !title.isEmpty()) {
            return jobRepository.findByTitleContainingIgnoreCase(title)
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        if (location != null && !location.isEmpty()) {
            return jobRepository.findByLocationContainingIgnoreCase(location)
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
//        return getAllActiveJobs();
        return getAllActiveJobs(0, 100).getContent();
    }

    // Job to Response mapper
    private JobResponse mapToResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .salary(job.getSalary())
                .jobType(job.getJobType())
                .experienceLevel(job.getExperienceLevel())
                .status(job.getStatus().name())
                .postedBy(job.getPostedBy())
                .createdAt(job.getCreatedAt())
                .build();
    }
}
