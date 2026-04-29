package com.ai.hiring.job_service.repository;



import com.ai.hiring.job_service.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatus(Job.JobStatus status);

    List<Job> findByPostedBy(String email);

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findByLocationContainingIgnoreCase(String location);

    List<Job> findByJobType(String jobType);

    List<Job> findByExperienceLevel(String experienceLevel);
}
