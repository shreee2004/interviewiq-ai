package com.interviewiq.interview.dto;

import com.interviewiq.interview.entity.Difficulty;
import com.interviewiq.interview.entity.InterviewSession;
import com.interviewiq.interview.entity.InterviewType;
import com.interviewiq.interview.entity.SessionStatus;
import java.time.Instant;
import java.util.UUID;

/**
 * List-view row (GET /interviews) — deliberately excludes the transcript. Resolving
 * jobRoleId/companyId into display names is left to the client, which fetches
 * GET /interviews/roles and /companies once and caches them locally rather than this
 * endpoint doing a join per row.
 */
public record SessionSummaryResponse(
        UUID id,
        UUID jobRoleId,
        UUID companyId,
        InterviewType interviewType,
        Difficulty difficulty,
        SessionStatus status,
        Instant startedAt,
        Instant completedAt,
        Instant createdAt) {

    public static SessionSummaryResponse from(InterviewSession session) {
        return new SessionSummaryResponse(
                session.getId(),
                session.getJobRoleId(),
                session.getCompanyId(),
                session.getInterviewType(),
                session.getDifficulty(),
                session.getStatus(),
                session.getStartedAt(),
                session.getCompletedAt(),
                session.getCreatedAt());
    }
}
