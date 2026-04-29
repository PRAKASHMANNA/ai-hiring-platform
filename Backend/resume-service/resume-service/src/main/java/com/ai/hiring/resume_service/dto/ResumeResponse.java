package com.ai.hiring.resume_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponse {

    private String id;
    private String candidateEmail;
    private String candidateName;
    private String fileName;
    private String fileUrl;
    private List<String> skills;
    private String experience;
    private String education;
    private Double aiMatchScore;
    private String aiSummary;
    private String jobId;
    private String jobTitle;
    private String status;
    private LocalDateTime uploadedAt;
    private LocalDateTime analyzedAt;
}
