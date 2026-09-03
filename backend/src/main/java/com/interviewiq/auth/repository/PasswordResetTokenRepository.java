package com.interviewiq.auth.repository;

import com.interviewiq.auth.entity.PasswordResetToken;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    Optional<PasswordResetToken> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);

    // A user can have multiple outstanding reset tokens (each forgot-password call issues a
    // new one without invalidating prior ones — see docs/API_DESIGN.md §2). Without this,
    // an older unconsumed token stays fully valid even after a newer one is used to reset
    // the password, letting a stale/leaked link grant a second, unauthorized password change.
    @Modifying
    @Query("update PasswordResetToken t set t.consumedAt = :now where t.userId = :userId and t.consumedAt is null")
    void consumeAllActiveForUser(@Param("userId") UUID userId, @Param("now") Instant now);
}
