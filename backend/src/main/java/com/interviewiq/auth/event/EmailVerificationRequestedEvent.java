package com.interviewiq.auth.event;

import java.util.UUID;

/**
 * Published whenever a verification token is issued (on register, and on resend). Carries
 * the raw token because {@code notification} — the listener that actually sends the email
 * (see docs/API_DESIGN.md §2, notification's package-info) — never touches the auth
 * module's repositories; {@code auth} only ever persists the token's hash, so the raw
 * value exists nowhere else once this event is published.
 */
public record EmailVerificationRequestedEvent(UUID userId, String email, String rawToken) {}
