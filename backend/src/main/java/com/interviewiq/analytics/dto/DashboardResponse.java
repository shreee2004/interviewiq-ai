package com.interviewiq.analytics.dto;

import com.interviewiq.interview.dto.SessionSummaryResponse;
import java.util.List;

/**
 * docs/API_DESIGN.md §8 describes this as a "widget bundle: streak, avg scores, weekly
 * progress, recent interviews." Only streak and recent interviews are real data sources
 * right now — avg scores and weekly progress both derive from {@code answer_evaluations},
 * which nothing populates until the {@code ai} module (Phase 4) exists. Rather than invent
 * a shape for data that doesn't exist yet, those two widgets are left out of this response
 * entirely until then.
 */
public record DashboardResponse(StreakResponse streak, List<SessionSummaryResponse> recentInterviews) {}
