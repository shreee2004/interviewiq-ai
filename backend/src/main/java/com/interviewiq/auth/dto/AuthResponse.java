package com.interviewiq.auth.dto;

import java.util.UUID;

/**
 * Returned by register/login/refresh. The refresh token itself is never in this body —
 * it's set as an httpOnly cookie (docs/API_DESIGN.md §1) so client-side JS can't read it.
 */
public record AuthResponse(String accessToken, long expiresInSeconds, UUID userId, String email, String role) {}
