package com.interviewiq.auth.service;

import com.interviewiq.auth.dto.AuthResponse;
import java.time.Instant;

/** Internal to the auth module — the raw refresh token never leaves as JSON, only as a cookie (see AuthController). */
public record AuthResult(AuthResponse body, String rawRefreshToken, Instant refreshTokenExpiresAt) {}
