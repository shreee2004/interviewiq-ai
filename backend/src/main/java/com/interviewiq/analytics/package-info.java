/**
 * Read-side aggregation for the dashboard: streaks, skill radar, trends, leaderboard,
 * activity heatmap. Reads from {@code user_skill_scores}, {@code user_streaks},
 * {@code user_xp} (docs/DATABASE.md §2.8); leaderboard results are cached in Redis
 * with a short TTL rather than stored. See docs/API_DESIGN.md §8.
 */
package com.interviewiq.analytics;
