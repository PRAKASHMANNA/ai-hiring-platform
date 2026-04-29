package com.ai.hiring.application_service.repository;


import com.ai.hiring.application_service.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByCandidateEmail(String email);

    List<JobApplication> findByJobId(Long jobId);

    Optional<JobApplication> findByCandidateEmailAndJobId(String email, Long jobId);

    List<JobApplication> findByStatus(JobApplication.ApplicationStatus status);
}
