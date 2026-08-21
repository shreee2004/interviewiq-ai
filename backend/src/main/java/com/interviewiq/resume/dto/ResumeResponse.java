package com.interviewiq.resume.dto;

import com.interviewiq.resume.entity.Resume;
import java.time.Instant;
import java.util.UUID;

/**
 * {@code status} is derived, not stored — {@code parsedAt == null} means parsing hasn't
 * run yet (docs/API_DESIGN.md §4: "frontend polls GET /resumes/{id} (status field)").
 * Real parsing lands with the {@code ai} module in Phase 4; until then every resume stays
 * PENDING forever, which is the correct state for a not-yet-built feature.
 */
public record ResumeResponse(
        UUID id, String originalFilename, boolean active, String status, Instant parsedAt, Instant createdAt) {

    public static ResumeResponse from(Resume resume) {
        return new ResumeResponse(
                resume.getId(),
                resume.getOriginalFilename(),
                resume.isActive(),
                resume.getParsedAt() == null ? "PENDING" : "PARSED",
                resume.getParsedAt(),
                resume.getCreatedAt());
    }
}
