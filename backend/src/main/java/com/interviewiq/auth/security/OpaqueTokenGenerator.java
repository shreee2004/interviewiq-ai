package com.interviewiq.auth.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Component;

/**
 * Generates and hashes the opaque, high-entropy random values used for refresh tokens,
 * email verification tokens, and password reset tokens (docs/DATABASE.md §2.1). Only the
 * SHA-256 hash is ever persisted — the raw value is shown to the client once and never
 * stored, so a database read alone can't be used to forge a session.
 */
@Component
public class OpaqueTokenGenerator {

    private static final int TOKEN_BYTES = 32; // 256 bits
    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}
