package com.interviewiq.analytics.dto;

import com.interviewiq.analytics.entity.UserStreak;
import java.time.LocalDate;

public record StreakResponse(int currentStreakDays, int longestStreakDays, LocalDate lastActivityDate) {

    public static final StreakResponse NONE = new StreakResponse(0, 0, null);

    public static StreakResponse from(UserStreak streak) {
        return new StreakResponse(streak.getCurrentStreakDays(), streak.getLongestStreakDays(), streak.getLastActivityDate());
    }
}
