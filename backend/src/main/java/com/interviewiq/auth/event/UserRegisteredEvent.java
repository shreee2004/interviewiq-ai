package com.interviewiq.auth.event;

import java.util.UUID;

/**
 * Published on successful registration. Lets other modules (e.g. {@code notification}'s
 * welcome message) react without {@code auth} depending on them — see
 * docs/ARCHITECTURE.md §3's cross-feature-call rule, and
 * {@code com.interviewiq.interview.event.SessionStartedEvent} for the same pattern.
 */
public record UserRegisteredEvent(UUID userId, String email) {}
