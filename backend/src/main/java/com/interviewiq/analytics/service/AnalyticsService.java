package com.interviewiq.analytics.service;

import com.interviewiq.analytics.dto.DashboardResponse;
import com.interviewiq.analytics.dto.StreakResponse;
import com.interviewiq.analytics.entity.UserStreak;
import com.interviewiq.analytics.repository.UserStreakRepository;
import com.interviewiq.interview.event.SessionStartedEvent;
import com.interviewiq.interview.service.InterviewService;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnalyticsService {

    private static final int RECENT_INTERVIEWS_LIMIT = 5;

    private final UserStreakRepository streakRepository;
    private final InterviewService interviewService;

    public AnalyticsService(UserStreakRepository streakRepository, InterviewService interviewService) {
        this.streakRepository = streakRepository;
        this.interviewService = interviewService;
    }

    /**
     * Reacts to {@link SessionStartedEvent} rather than being called directly by
     * {@code interview} — the direct-call direction would form a cycle, since this service
     * already depends on {@link InterviewService} for {@link #getDashboard}. Runs
     * synchronously, in the same transaction as the publishing {@code startSession} call
     * (Spring's default for {@code @EventListener}), so a failure here rolls the session
     * start back too — recording activity is treated as part of "starting a session", not
     * a best-effort side effect.
     */
    @EventListener
    public void onSessionStarted(SessionStartedEvent event) {
        recordActivity(event.userId());
    }

    private void recordActivity(UUID userId) {
        LocalDate today = LocalDate.now();
        UserStreak streak =
                streakRepository.findById(userId).orElseGet(() -> UserStreak.builder().userId(userId).build());
        LocalDate lastActivityDate = streak.getLastActivityDate();

        if (today.equals(lastActivityDate)) {
            return; // already recorded today's activity — avoid double-counting a streak day
        }
        if (lastActivityDate != null && lastActivityDate.equals(today.minusDays(1))) {
            streak.setCurrentStreakDays(streak.getCurrentStreakDays() + 1);
        } else {
            streak.setCurrentStreakDays(1); // first-ever activity, or the streak was broken
        }
        streak.setLongestStreakDays(Math.max(streak.getLongestStreakDays(), streak.getCurrentStreakDays()));
        streak.setLastActivityDate(today);
        streakRepository.save(streak);
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(UUID userId) {
        StreakResponse streak = streakRepository.findById(userId).map(StreakResponse::from).orElse(StreakResponse.NONE);
        Pageable recentPageable = PageRequest.of(0, RECENT_INTERVIEWS_LIMIT, Sort.by(Sort.Direction.DESC, "createdAt"));
        var recentInterviews = interviewService.listSessions(userId, null, recentPageable).content();
        return new DashboardResponse(streak, recentInterviews);
    }
}
