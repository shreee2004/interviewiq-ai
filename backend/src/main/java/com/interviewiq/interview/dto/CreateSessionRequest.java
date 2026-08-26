package com.interviewiq.interview.dto;

import com.interviewiq.interview.entity.Difficulty;
import com.interviewiq.interview.entity.InterviewType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateSessionRequest(
        UUID resumeId,
        UUID jobRoleId,
        UUID companyId,
        @NotNull InterviewType interviewType,
        @NotNull Difficulty difficulty,
        @Size(max = 30) String language,
        @Positive @Max(180) int plannedDurationMinutes) {}
