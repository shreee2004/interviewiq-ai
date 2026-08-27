package com.interviewiq.interview.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Published when a session transitions PENDING -> IN_PROGRESS. Lets other modules (e.g.
 * {@code analytics}'s streak tracking) react without {@code interview} depending on them —
 * see docs/ARCHITECTURE.md §3's cross-feature-call rule. Any module may listen; today only
 * {@code analytics} does.
 */
public record SessionStartedEvent(UUID userId, UUID sessionId, Instant occurredAt) {}
