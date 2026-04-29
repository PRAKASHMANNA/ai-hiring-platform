package com.ai.hiring.application_service.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRequest {

    @NotNull(message = "Job ID is required")
    private Long jobId;

    private String jobTitle;

    private String resumeId;

    private Double aiMatchScore;
}
