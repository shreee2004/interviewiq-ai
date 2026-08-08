package com.interviewiq.auth.controller;

import com.interviewiq.auth.dto.AuthResponse;
import com.interviewiq.auth.dto.LoginRequest;
import com.interviewiq.auth.dto.RegisterRequest;
import com.interviewiq.auth.dto.SessionResponse;
import com.interviewiq.auth.service.AuthResult;
import com.interviewiq.auth.service.AuthService;
import com.interviewiq.config.InterviewIqProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** See docs/API_DESIGN.md §2. Endpoints beyond the core token lifecycle (email verification,
 * password reset, Google OAuth, 2FA) land in a later Phase 2 slice. */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final String REFRESH_COOKIE_PATH = "/api/v1/auth";

    private final AuthService authService;
    private final InterviewIqProperties properties;

    public AuthController(AuthService authService, InterviewIqProperties properties) {
        this.authService = authService;
        this.properties = properties;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        AuthResult result = authService.register(request, deviceLabel(httpRequest), clientIp(httpRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.rawRefreshToken(), result.refreshTokenExpiresAt()).toString())
                .body(result.body());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        AuthResult result = authService.login(request, deviceLabel(httpRequest), clientIp(httpRequest));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.rawRefreshToken(), result.refreshTokenExpiresAt()).toString())
                .body(result.body());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken, HttpServletRequest httpRequest) {
        AuthResult result = authService.refresh(refreshToken, deviceLabel(httpRequest), clientIp(httpRequest));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(result.rawRefreshToken(), result.refreshTokenExpiresAt()).toString())
                .body(result.body());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, expiredRefreshCookie().toString())
                .build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal String userId) {
        authService.logoutAll(UUID.fromString(userId));
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, expiredRefreshCookie().toString())
                .build();
    }

    @GetMapping("/sessions")
    public List<SessionResponse> sessions(
            @AuthenticationPrincipal String userId,
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken) {
        return authService.listSessions(UUID.fromString(userId), refreshToken);
    }

    private ResponseCookie refreshCookie(String value, Instant expiresAt) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(properties.jwt().refreshCookieSecure())
                .sameSite("Lax")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(Duration.between(Instant.now(), expiresAt))
                .build();
    }

    private ResponseCookie expiredRefreshCookie() {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(properties.jwt().refreshCookieSecure())
                .sameSite("Lax")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(Duration.ZERO)
                .build();
    }

    private String deviceLabel(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return null;
        }
        return userAgent.length() > 255 ? userAgent.substring(0, 255) : userAgent;
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
