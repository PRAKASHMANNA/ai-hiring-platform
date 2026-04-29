package com.ai.hiring.job_service.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Location is required")
    private String location;

    private String description;
    private String requirements;
    private String salary;
    private String jobType;
    private String experienceLevel;
}
