package com.ai.hiring.resume_service.dto;

import lombok.Data;

@Data
public class AnalyzeRequest {

    private String resumeId;
    private String jobId;
    private String jobTitle;
    private String jobDescription;
    private String jobRequirements;
}
