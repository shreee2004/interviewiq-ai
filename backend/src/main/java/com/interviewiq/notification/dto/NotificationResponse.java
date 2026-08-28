package com.interviewiq.notification.dto;

import com.interviewiq.notification.entity.Notification;
import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(UUID id, String type, String title, String body, boolean read, Instant createdAt) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.getReadAt() != null,
                notification.getCreatedAt());
    }
}
