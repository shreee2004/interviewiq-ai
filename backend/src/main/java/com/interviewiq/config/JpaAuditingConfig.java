package com.interviewiq.config;

import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Resolves createdBy/updatedBy (see {@link com.interviewiq.common.entity.BaseEntity})
 * from the authenticated principal's user id. The auth module (Phase 2) is expected
 * to authenticate requests with the user's UUID as the principal name; until then,
 * every write is attributed to no one (system/anonymous), which is correct for
 * pre-auth scaffolding and for background jobs run outside a request.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<UUID> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .flatMap(JpaAuditingConfig::tryParseUuid);
    }

    private static Optional<UUID> tryParseUuid(String value) {
        try {
            return Optional.of(UUID.fromString(value));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
