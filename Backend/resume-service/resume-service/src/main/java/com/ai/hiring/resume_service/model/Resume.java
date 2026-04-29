package com.ai.hiring.resume_service.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "resumes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume {

    @Id
    private String id;

    private String candidateEmail;

    private String candidateName;

    private String fileName;

    private String fileUrl;

    private String rawText;

    private List<String> skills;

    private String experience;

    private String education;

    private Double aiMatchScore;

    private String aiSummary;

    private String jobId;

    private String jobTitle;

    private ResumeStatus status;

    private LocalDateTime uploadedAt;

    private LocalDateTime analyzedAt;

    public enum ResumeStatus {
        UPLOADED, ANALYZING, ANALYZED, FAILED
    }
}
