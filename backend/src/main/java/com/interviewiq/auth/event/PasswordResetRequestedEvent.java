package com.interviewiq.auth.event;

import java.util.UUID;

/** Same shape/reasoning as {@link EmailVerificationRequestedEvent} — see that class's javadoc. */
public record PasswordResetRequestedEvent(UUID userId, String email, String rawToken) {}
