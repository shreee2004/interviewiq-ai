package com.interviewiq.resume.service;

import com.interviewiq.common.exception.InvalidFileException;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.resume.dto.ResumeResponse;
import com.interviewiq.resume.entity.Resume;
import com.interviewiq.resume.repository.ResumeRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeStorageService storageService;

    public ResumeService(ResumeRepository resumeRepository, ResumeStorageService storageService) {
        this.resumeRepository = resumeRepository;
        this.storageService = storageService;
    }

    public ResumeResponse upload(UUID userId, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new InvalidFileException("Uploaded file must have a filename");
        }

        resumeRepository.deactivateAllForUser(userId);

        Resume resume = Resume.builder()
                .userId(userId)
                .originalFilename(originalFilename)
                .fileUrl("pending")
                .active(true)
                .build();
        resumeRepository.save(resume);

        String storedPath = storageService.store(userId, resume.getId(), file);
        resume.setFileUrl(storedPath);

        // Actual parsing (extracting skills/experience/education into the resume's child
        // tables) is AI-driven and lands with the `ai` module in Phase 4 — parsedAt stays
        // null (status PENDING) until then.
        return ResumeResponse.from(resume);
    }

    @Transactional(readOnly = true)
    public List<ResumeResponse> list(UUID userId) {
        return resumeRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(ResumeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResumeResponse get(UUID userId, UUID resumeId) {
        return ResumeResponse.from(requireOwnedResume(userId, resumeId));
    }

    public ResumeResponse activate(UUID userId, UUID resumeId) {
        Resume resume = requireOwnedResume(userId, resumeId);
        resumeRepository.deactivateAllForUser(userId);
        resume.setActive(true);
        return ResumeResponse.from(resume);
    }

    public void delete(UUID userId, UUID resumeId) {
        Resume resume = requireOwnedResume(userId, resumeId);
        storageService.delete(resume.getFileUrl());
        resumeRepository.delete(resume);
    }

    private Resume requireOwnedResume(UUID userId, UUID resumeId) {
        return resumeRepository
                .findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Resume", resumeId));
    }
}
