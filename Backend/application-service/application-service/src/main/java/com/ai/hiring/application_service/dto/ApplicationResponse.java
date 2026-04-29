package com.ai.hiring.application_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private String candidateEmail;
    private String candidateName;
    private String resumeId;
    private Double aiMatchScore;
    private String status;
    private String recruiterNote;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
