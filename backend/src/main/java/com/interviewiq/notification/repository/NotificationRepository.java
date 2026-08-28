package com.interviewiq.notification.repository;

import com.interviewiq.notification.entity.Notification;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findAllByUserId(UUID userId, Pageable pageable);

    Page<Notification> findAllByUserIdAndReadAtIsNull(UUID userId, Pageable pageable);

    Page<Notification> findAllByUserIdAndReadAtIsNotNull(UUID userId, Pageable pageable);

    @Modifying
    @Query("update Notification n set n.readAt = :now where n.userId = :userId and n.readAt is null")
    void markAllRead(@Param("userId") UUID userId, @Param("now") Instant now);
}
