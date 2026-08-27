package com.interviewiq.analytics.controller;

import com.interviewiq.analytics.dto.DashboardResponse;
import com.interviewiq.analytics.service.AnalyticsService;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * See docs/API_DESIGN.md §8. Only {@code /dashboard} is implemented so far — skill-radar,
 * trends, leaderboard, and heatmap all depend on evaluation data that doesn't exist until
 * the {@code ai} module (Phase 4) lands.
 */
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard(@AuthenticationPrincipal String userId) {
        return analyticsService.getDashboard(UUID.fromString(userId));
    }
}
