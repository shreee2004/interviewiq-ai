package com.interviewiq.notification.controller;

import com.interviewiq.common.dto.PageResponse;
import com.interviewiq.notification.dto.NotificationResponse;
import com.interviewiq.notification.service.NotificationService;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** See docs/API_DESIGN.md §10. */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public PageResponse<NotificationResponse> list(
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) Boolean unread,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return notificationService.list(UUID.fromString(userId), unread, pageable);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markRead(@AuthenticationPrincipal String userId, @PathVariable UUID id) {
        return notificationService.markRead(UUID.fromString(userId), id);
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal String userId) {
        notificationService.markAllRead(UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }
}
