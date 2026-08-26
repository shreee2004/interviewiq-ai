package com.interviewiq.interview.dto;

import com.interviewiq.interview.entity.Difficulty;
import com.interviewiq.interview.entity.InterviewSession;
import com.interviewiq.interview.entity.InterviewTurn;
import com.interviewiq.interview.entity.InterviewType;
import com.interviewiq.interview.entity.SessionStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SessionDetailResponse(
        UUID id,
        UUID resumeId,
        UUID jobRoleId,
        UUID companyId,
        InterviewType interviewType,
        Difficulty difficulty,
        String language,
        int plannedDurationMinutes,
        SessionStatus status,
        Instant startedAt,
        Instant completedAt,
        Instant createdAt,
        List<TurnResponse> turns) {

    public static SessionDetailResponse from(InterviewSession session, List<InterviewTurn> turns) {
        return new SessionDetailResponse(
                session.getId(),
                session.getResumeId(),
                session.getJobRoleId(),
                session.getCompanyId(),
                session.getInterviewType(),
                session.getDifficulty(),
                session.getLanguage(),
                session.getPlannedDurationMinutes(),
                session.getStatus(),
                session.getStartedAt(),
                session.getCompletedAt(),
                session.getCreatedAt(),
                turns.stream().map(TurnResponse::from).toList());
    }
}
