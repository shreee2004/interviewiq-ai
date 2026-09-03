package com.interviewiq.auth.service;

import com.interviewiq.auth.dto.AuthResponse;
import com.interviewiq.auth.dto.LoginRequest;
import com.interviewiq.auth.dto.RegisterRequest;
import com.interviewiq.auth.dto.SessionResponse;
import com.interviewiq.auth.entity.EmailVerificationToken;
import com.interviewiq.auth.entity.PasswordResetToken;
import com.interviewiq.auth.entity.RefreshToken;
import com.interviewiq.auth.entity.User;
import com.interviewiq.auth.entity.UserStatus;
import com.interviewiq.auth.event.EmailVerificationRequestedEvent;
import com.interviewiq.auth.event.PasswordResetRequestedEvent;
import com.interviewiq.auth.event.UserRegisteredEvent;
import com.interviewiq.auth.repository.EmailVerificationTokenRepository;
import com.interviewiq.auth.repository.PasswordResetTokenRepository;
import com.interviewiq.auth.repository.RefreshTokenRepository;
import com.interviewiq.auth.repository.UserRepository;
import com.interviewiq.auth.security.JwtService;
import com.interviewiq.auth.security.OpaqueTokenGenerator;
import com.interviewiq.common.exception.BusinessRuleException;
import com.interviewiq.common.exception.ConflictException;
import com.interviewiq.config.InterviewIqProperties;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OpaqueTokenGenerator opaqueTokenGenerator;
    private final InterviewIqProperties properties;
    private final ApplicationEventPublisher eventPublisher;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            EmailVerificationTokenRepository emailVerificationTokenRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            OpaqueTokenGenerator opaqueTokenGenerator,
            InterviewIqProperties properties,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.opaqueTokenGenerator = opaqueTokenGenerator;
        this.properties = properties;
        this.eventPublisher = eventPublisher;
    }

    public AuthResult register(RegisterRequest request, String deviceLabel, String ipAddress) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("An account with this email already exists");
        }
        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);
        eventPublisher.publishEvent(new UserRegisteredEvent(user.getId(), user.getEmail()));
        issueVerificationToken(user);
        return issueTokens(user, deviceLabel, ipAddress, false);
    }

    /**
     * No-ops silently for an unknown or already-verified email — same enumeration-safety
     * reasoning as {@link #login} — and also for one still inside its resend cooldown, so
     * a caller spamming this endpoint can't email-bomb an arbitrary address (no real rate
     * limiting exists anywhere in this codebase yet; see application.yml's comment on
     * {@code email-verification-resend-cooldown-seconds}).
     */
    public void resendVerification(String email) {
        userRepository.findByEmail(email)
                .filter(user -> !user.isEmailVerified())
                .filter(user -> !isWithinResendCooldown(user.getId()))
                .ifPresent(this::issueVerificationToken);
    }

    private boolean isWithinResendCooldown(UUID userId) {
        Instant cooldownEnd = Instant.now().minusSeconds(properties.auth().emailVerificationResendCooldownSeconds());
        return emailVerificationTokenRepository
                .findFirstByUserIdOrderByCreatedAtDesc(userId)
                .map(token -> token.getCreatedAt().isAfter(cooldownEnd))
                .orElse(false);
    }

    public void verifyEmail(String rawToken) {
        EmailVerificationToken token = emailVerificationTokenRepository
                .findByTokenHash(opaqueTokenGenerator.hash(rawToken))
                .filter(EmailVerificationToken::isActive)
                .orElseThrow(() -> new BusinessRuleException("Invalid or expired verification token"));

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new BusinessRuleException("Invalid or expired verification token"));
        user.setEmailVerified(true);

        token.setConsumedAt(Instant.now());
    }

    private void issueVerificationToken(User user) {
        String rawToken = opaqueTokenGenerator.generate();
        EmailVerificationToken token = EmailVerificationToken.builder()
                .userId(user.getId())
                .tokenHash(opaqueTokenGenerator.hash(rawToken))
                .expiresAt(Instant.now().plus(Duration.ofHours(properties.auth().emailVerificationTokenTtlHours())))
                .build();
        emailVerificationTokenRepository.save(token);
        eventPublisher.publishEvent(new EmailVerificationRequestedEvent(user.getId(), user.getEmail(), rawToken));
    }

    /** No-ops silently for an unknown email or one still inside its resend cooldown — same reasoning as {@link #resendVerification}. */
    public void forgotPassword(String email) {
        userRepository.findByEmail(email)
                .filter(user -> !isWithinPasswordResetCooldown(user.getId()))
                .ifPresent(user -> {
                    String rawToken = opaqueTokenGenerator.generate();
                    PasswordResetToken token = PasswordResetToken.builder()
                            .userId(user.getId())
                            .tokenHash(opaqueTokenGenerator.hash(rawToken))
                            .expiresAt(Instant.now().plus(Duration.ofHours(properties.auth().passwordResetTokenTtlHours())))
                            .build();
                    passwordResetTokenRepository.save(token);
                    eventPublisher.publishEvent(new PasswordResetRequestedEvent(user.getId(), user.getEmail(), rawToken));
                });
    }

    private boolean isWithinPasswordResetCooldown(UUID userId) {
        Instant cooldownEnd = Instant.now().minusSeconds(properties.auth().passwordResetResendCooldownSeconds());
        return passwordResetTokenRepository
                .findFirstByUserIdOrderByCreatedAtDesc(userId)
                .map(token -> token.getCreatedAt().isAfter(cooldownEnd))
                .orElse(false);
    }

    /**
     * Also consumes every other outstanding reset token for the user (not just the one used)
     * and revokes every active refresh token — a password reset should invalidate every other
     * way of getting into the account, not just end the caller's own session.
     */
    public void resetPassword(String rawToken, String newPassword) {
        PasswordResetToken token = passwordResetTokenRepository
                .findByTokenHash(opaqueTokenGenerator.hash(rawToken))
                .filter(PasswordResetToken::isActive)
                .orElseThrow(() -> new BusinessRuleException("Invalid or expired reset token"));

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new BusinessRuleException("Invalid or expired reset token"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));

        Instant now = Instant.now();
        passwordResetTokenRepository.consumeAllActiveForUser(user.getId(), now);
        refreshTokenRepository.revokeAllActiveForUser(user.getId(), now);
    }

    public AuthResult login(LoginRequest request, String deviceLabel, String ipAddress) {
        User user = userRepository.findByEmail(request.email())
                // same generic message as a bad password — never reveal whether the email exists
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (user.getPasswordHash() == null
                || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("This account is " + user.getStatus().name().toLowerCase());
        }
        return issueTokens(user, deviceLabel, ipAddress, request.rememberMe());
    }

    public AuthResult refresh(String rawRefreshToken, String deviceLabel, String ipAddress) {
        RefreshToken token = findActiveToken(rawRefreshToken);
        token.setRevokedAt(Instant.now());
        refreshTokenRepository.save(token);

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("This account is " + user.getStatus().name().toLowerCase());
        }
        // Rotation always reissues at the standard TTL — remember-me only affects the
        // session length chosen at login, not how long each subsequent rotation lives.
        return issueTokens(user, deviceLabel, ipAddress, false);
    }

    public void logout(String rawRefreshToken) {
        if (rawRefreshToken == null) {
            return;
        }
        refreshTokenRepository.findByTokenHash(opaqueTokenGenerator.hash(rawRefreshToken))
                .filter(token -> token.getRevokedAt() == null)
                .ifPresent(token -> {
                    token.setRevokedAt(Instant.now());
                    refreshTokenRepository.save(token);
                });
    }

    public void logoutAll(UUID userId) {
        refreshTokenRepository.revokeAllActiveForUser(userId, Instant.now());
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> listSessions(UUID userId, String currentRawRefreshToken) {
        String currentHash = currentRawRefreshToken == null ? null : opaqueTokenGenerator.hash(currentRawRefreshToken);
        return refreshTokenRepository
                .findAllByUserIdAndRevokedAtIsNullAndExpiresAtAfter(userId, Instant.now())
                .stream()
                .map(token -> new SessionResponse(
                        token.getId(),
                        token.getDeviceLabel(),
                        token.getIpAddress(),
                        token.getCreatedAt(),
                        token.getExpiresAt(),
                        token.getTokenHash().equals(currentHash)))
                .toList();
    }

    private RefreshToken findActiveToken(String rawRefreshToken) {
        if (rawRefreshToken == null) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        RefreshToken token = refreshTokenRepository
                .findByTokenHash(opaqueTokenGenerator.hash(rawRefreshToken))
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        if (!token.isActive()) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        return token;
    }

    private AuthResult issueTokens(User user, String deviceLabel, String ipAddress, boolean rememberMe) {
        String accessToken = jwtService.generateAccessToken(user);

        String rawRefreshToken = opaqueTokenGenerator.generate();
        int ttlDays = rememberMe
                ? properties.jwt().rememberMeRefreshTokenTtlDays()
                : properties.jwt().refreshTokenTtlDays();
        Instant expiresAt = Instant.now().plus(Duration.ofDays(ttlDays));

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(opaqueTokenGenerator.hash(rawRefreshToken))
                .deviceLabel(deviceLabel)
                .ipAddress(ipAddress)
                .expiresAt(expiresAt)
                .build();
        refreshTokenRepository.save(refreshToken);

        AuthResponse body = new AuthResponse(
                accessToken, jwtService.accessTokenTtlSeconds(), user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResult(body, rawRefreshToken, expiresAt);
    }
}
