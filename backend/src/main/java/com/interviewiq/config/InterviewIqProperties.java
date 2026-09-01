package com.interviewiq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed binding for the {@code interviewiq.*} block in application.yml. Records are
 * bound via their canonical constructor automatically — no {@code @ConstructorBinding}
 * needed (that annotation is only for non-record classes with multiple constructors).
 */
@ConfigurationProperties(prefix = "interviewiq")
public record InterviewIqProperties(
        Jwt jwt,
        Auth auth,
        Oauth oauth,
        Ai ai,
        RateLimit rateLimit,
        Cors cors,
        Storage storage,
        Notification notification,
        String frontendBaseUrl) {

    public record Jwt(
            String secret,
            int accessTokenTtlMinutes,
            int refreshTokenTtlDays,
            int rememberMeRefreshTokenTtlDays,
            boolean refreshCookieSecure) {}

    public record Auth(int emailVerificationTokenTtlHours, int emailVerificationResendCooldownSeconds) {}

    public record Oauth(Google google) {
        public record Google(String clientId, String clientSecret) {}
    }

    public record Ai(String provider, Openai openai, Gemini gemini) {
        public record Openai(String apiKey, String model) {}

        public record Gemini(String apiKey, String model) {}
    }

    public record RateLimit(int defaultRequestsPerMinute, int aiEndpointRequestsPerMinute) {}

    public record Cors(String allowedOrigins) {}

    /**
     * Local-disk storage for uploaded files (resumes, avatars later). A placeholder for
     * Phase 6/7 — production swaps this for object storage (S3-compatible) without
     * changing the {@code resume} module's public interface.
     */
    public record Storage(String resumeUploadDir) {}

    public record Notification(String mailFrom) {}
}
