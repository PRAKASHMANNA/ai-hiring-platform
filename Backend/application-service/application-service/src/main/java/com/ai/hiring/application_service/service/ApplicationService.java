package com.ai.hiring.application_service.service;


import com.ai.hiring.application_service.dto.ApplicationRequest;
import com.ai.hiring.application_service.dto.ApplicationResponse;
import com.ai.hiring.application_service.model.JobApplication;
import com.ai.hiring.application_service.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Job pe apply karo
    public ApplicationResponse applyForJob(ApplicationRequest request,
                                           String candidateEmail,
                                           String candidateName) {

        // Already applied check karo
        applicationRepository.findByCandidateEmailAndJobId(
                        candidateEmail, request.getJobId())
                .ifPresent(app -> {
                    throw new RuntimeException("Already applied for this job!");
                });

        JobApplication application = JobApplication.builder()
                .jobId(request.getJobId())
                .jobTitle(request.getJobTitle())
                .candidateEmail(candidateEmail)
                .candidateName(candidateName)
                .resumeId(request.getResumeId())
                .aiMatchScore(request.getAiMatchScore())
                .build();

        JobApplication saved = applicationRepository.save(application);

        // Kafka event bhejo — notification service ko
//        kafkaTemplate.send("application-events", candidateEmail,
//                "New application submitted for job: " + request.getJobTitle()
//                        + " by " + candidateEmail);
        kafkaTemplate.send("application-events", candidateEmail,
                candidateEmail + "::" +
                        "Your application for '" + request.getJobTitle() +
                        "' has been submitted successfully! We will review it soon.");

        return mapToResponse(saved);
    }

    // Candidate ke saare applications
    public List<ApplicationResponse> getMyApplications(String email) {
        return applicationRepository.findByCandidateEmail(email)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Recruiter — job ke saare applications dekho
    public List<ApplicationResponse> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Status update karo (Recruiter)
    public ApplicationResponse updateStatus(Long id,
                                            String status,
                                            String recruiterNote) {

        JobApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(JobApplication.ApplicationStatus.valueOf(status));
        application.setRecruiterNote(recruiterNote);

        JobApplication updated = applicationRepository.save(application);

        // Kafka event bhejo — candidate ko notify karo
        /*kafkaTemplate.send("application-events",
                application.getCandidateEmail(),
                "Your application status updated to: " + status
                        + " for job: " + application.getJobTitle());*/
        kafkaTemplate.send("application-events",
                application.getCandidateEmail(),
                application.getCandidateEmail() + "::" +
                        "Your application for '" + application.getJobTitle() +
                        "' has been updated to: " + status + ". " +
                        (status.equals("SHORTLISTED") ? "Congratulations! You have been shortlisted for interview!" :
                                status.equals("HIRED") ? "Congratulations! You have been hired!" :
                                        status.equals("REJECTED") ? "Thank you for applying. Better luck next time!" :
                                                "Your application is under review."));
        return mapToResponse(updated);
    }

    // Mapper
    private ApplicationResponse mapToResponse(JobApplication app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .jobId(app.getJobId())
                .jobTitle(app.getJobTitle())
                .candidateEmail(app.getCandidateEmail())
                .candidateName(app.getCandidateName())
                .resumeId(app.getResumeId())
                .aiMatchScore(app.getAiMatchScore())
                .status(app.getStatus().name())
                .recruiterNote(app.getRecruiterNote())
                .appliedAt(app.getAppliedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }
}
