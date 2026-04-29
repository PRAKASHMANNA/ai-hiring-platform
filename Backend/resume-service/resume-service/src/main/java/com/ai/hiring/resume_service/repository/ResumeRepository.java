package com.ai.hiring.resume_service.repository;


import com.ai.hiring.resume_service.model.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends MongoRepository<Resume, String> {

    List<Resume> findByCandidateEmail(String email);

    List<Resume> findByJobId(String jobId);

    Optional<Resume> findByCandidateEmailAndJobId(String email, String jobId);

    List<Resume> findByStatus(Resume.ResumeStatus status);
}
