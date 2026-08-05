/**
 * Admin panel endpoints: user management, platform analytics, AI cost/usage,
 * feature flags, system log search. Every endpoint here requires {@code ROLE_ADMIN}
 * (docs/API_DESIGN.md §11). Owns {@code feature_flags}, {@code api_usage_logs},
 * {@code system_logs} (docs/DATABASE.md §2.10).
 */
package com.interviewiq.admin;
