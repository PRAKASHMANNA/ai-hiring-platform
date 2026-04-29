package com.ai.hiring.job_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private Long id;
    private String title;
    private String company;
    private String location;
    private String description;
    private String requirements;
    private String salary;
    private String jobType;
    private String experienceLevel;
    private String status;
    private String postedBy;
    private LocalDateTime createdAt;
}