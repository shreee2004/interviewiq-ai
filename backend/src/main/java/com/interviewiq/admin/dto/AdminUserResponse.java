package com.interviewiq.admin.dto;

import com.interviewiq.auth.entity.User;
import java.time.Instant;
import java.util.UUID;

public record AdminUserResponse(UUID id, String email, boolean emailVerified, String role, String status, Instant createdAt) {

    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getCreatedAt());
    }
}
