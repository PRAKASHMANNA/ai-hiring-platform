package com.ai.hiring.resume_service.service;

import com.ai.hiring.resume_service.dto.AnalyzeRequest;
import com.ai.hiring.resume_service.dto.ResumeResponse;
import com.ai.hiring.resume_service.model.Resume;
import com.ai.hiring.resume_service.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;

    // Resume upload karo
    public ResumeResponse uploadResume(MultipartFile file,
                                       String candidateEmail,
                                       String candidateName) throws IOException {

        // File text extract karo (basic)
        //String rawText = new String(file.getBytes());
       /* String rawText;
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.toLowerCase().endsWith(".pdf")) {
            PDDocument document = Loader.loadPDF(file.getBytes());
            PDFTextStripper stripper = new PDFTextStripper();
            rawText = stripper.getText(document);
            document.close();
        } else {
            rawText = new String(file.getBytes());
        }*/
        // File text extract karo
        String rawText;
        String originalFilename = file.getOriginalFilename().toLowerCase();

        if (originalFilename.endsWith(".pdf")) {
            PDDocument document = Loader.loadPDF(file.getBytes());
            PDFTextStripper stripper = new PDFTextStripper();
            rawText = stripper.getText(document);
            document.close();

        } else if (originalFilename.endsWith(".docx")) {
            XWPFDocument docx = new XWPFDocument(file.getInputStream());
            XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
            rawText = extractor.getText();
            extractor.close();

        } else if (originalFilename.endsWith(".doc")) {
            HWPFDocument doc = new HWPFDocument(file.getInputStream());
            WordExtractor extractor = new WordExtractor(doc);
            rawText = extractor.getText();
            extractor.close();

        } else if (originalFilename.endsWith(".txt")) {
            rawText = new String(file.getBytes());

        } else {
            throw new RuntimeException("Unsupported file format! Only PDF, DOCX, DOC, TXT allowed.");
        }
        // Skills extract karo (basic keyword matching)
        List<String> skills = extractSkills(rawText);

        Resume resume = Resume.builder()
                .candidateEmail(candidateEmail)
                .candidateName(candidateName)
                .fileName(file.getOriginalFilename())
                .fileUrl("local/" + file.getOriginalFilename())
                .rawText(rawText)
                .skills(skills)
                .status(Resume.ResumeStatus.UPLOADED)
                .uploadedAt(LocalDateTime.now())
                .build();

        Resume saved = resumeRepository.save(resume);
        return mapToResponse(saved);
    }

    // AI Analysis karo
    public ResumeResponse analyzeResume(AnalyzeRequest request,
                                        String candidateEmail) {

        Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        // Match score calculate karo
        double matchScore = calculateMatchScore(
                resume.getRawText(),
                request.getJobRequirements()
        );

        // AI Summary banao
        String summary = generateSummary(resume, request, matchScore);

        resume.setAiMatchScore(matchScore);
        resume.setAiSummary(summary);
        resume.setJobId(request.getJobId());
        resume.setJobTitle(request.getJobTitle());
        resume.setStatus(Resume.ResumeStatus.ANALYZED);
        resume.setAnalyzedAt(LocalDateTime.now());

        Resume updated = resumeRepository.save(resume);
        return mapToResponse(updated);
    }

    // Candidate ke saare resumes
    public List<ResumeResponse> getMyResumes(String email) {
        return resumeRepository.findByCandidateEmail(email)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Single resume by ID
    public ResumeResponse getResumeById(String id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
        return mapToResponse(resume);
    }

    // Skills extract karo resume text se
    private List<String> extractSkills(String text) {
        List<String> allSkills = Arrays.asList(
                "java", "spring", "spring boot", "mysql", "mongodb",
                "react", "javascript", "python", "docker", "kubernetes",
                "aws", "kafka", "microservices", "rest api", "git",
                "html", "css", "bootstrap", "maven", "hibernate"
        );

        String lowerText = text.toLowerCase();
        return allSkills.stream()
                .filter(lowerText::contains)
                .collect(Collectors.toList());
    }

    // Match score calculate karo
    private double calculateMatchScore(String resumeText,
                                       String jobRequirements) {
        if (jobRequirements == null || jobRequirements.isEmpty()) {
            return 50.0;
        }

        String[] requirements = jobRequirements.toLowerCase().split(",");
        String lowerResume = resumeText.toLowerCase();

        long matchCount = Arrays.stream(requirements)
                .map(String::trim)
                .filter(lowerResume::contains)
                .count();

        double score = ((double) matchCount / requirements.length) * 100;
        return Math.round(score * 10.0) / 10.0;
    }

    // Summary generate karo
    private String generateSummary(Resume resume,
                                   AnalyzeRequest request,
                                   double matchScore) {
        return String.format(
                "Candidate %s has a %.1f%% match for the position of %s. " +
                        "Detected skills: %s. " +
                        "Recommendation: %s",
                resume.getCandidateName(),
                matchScore,
                request.getJobTitle(),
                String.join(", ", resume.getSkills()),
                matchScore >= 70 ? "Strong candidate, recommended for interview" :
                        matchScore >= 40 ? "Average match, consider for screening" :
                                "Low match, may not be suitable for this role"
        );
    }

    // Resume to Response mapper
    private ResumeResponse mapToResponse(Resume resume) {
        return ResumeResponse.builder()
                .id(resume.getId())
                .candidateEmail(resume.getCandidateEmail())
                .candidateName(resume.getCandidateName())
                .fileName(resume.getFileName())
                .fileUrl(resume.getFileUrl())
                .skills(resume.getSkills())
                .experience(resume.getExperience())
                .education(resume.getEducation())
                .aiMatchScore(resume.getAiMatchScore())
                .aiSummary(resume.getAiSummary())
                .jobId(resume.getJobId())
                .jobTitle(resume.getJobTitle())
                .status(resume.getStatus().name())
                .uploadedAt(resume.getUploadedAt())
                .analyzedAt(resume.getAnalyzedAt())
                .build();
    }
}
