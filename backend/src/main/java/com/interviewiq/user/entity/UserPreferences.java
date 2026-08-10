package com.interviewiq.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** PK is the owning user's id — see {@link UserProfile} for why. */
@Entity
@Table(name = "user_preferences")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferences {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Theme theme = Theme.SYSTEM;

    @Builder.Default
    @Column(nullable = false, length = 10)
    private String language = "en";

    @Builder.Default
    @Column(name = "email_notifications", nullable = false)
    private boolean emailNotifications = true;

    @Builder.Default
    @Column(name = "push_notifications", nullable = false)
    private boolean pushNotifications = true;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
