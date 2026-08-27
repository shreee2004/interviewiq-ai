package com.interviewiq.admin.controller;

import com.interviewiq.admin.dto.AdminUserResponse;
import com.interviewiq.admin.dto.FeatureFlagResponse;
import com.interviewiq.admin.dto.UpdateFeatureFlagRequest;
import com.interviewiq.admin.dto.UpdateUserStatusRequest;
import com.interviewiq.admin.service.AdminService;
import com.interviewiq.auth.entity.UserStatus;
import com.interviewiq.common.dto.PageResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** See docs/API_DESIGN.md §11. Every endpoint here requires ROLE_ADMIN (enforced in SecurityConfig). */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public PageResponse<AdminUserResponse> users(
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String email,
            // Without a default sort, row order across pages is undefined once a client
            // doesn't specify one — Postgres makes no ordering guarantee without ORDER BY.
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return adminService.listUsers(status, email, pageable);
    }

    @PatchMapping("/users/{id}/status")
    public AdminUserResponse updateUserStatus(@PathVariable UUID id, @Valid @RequestBody UpdateUserStatusRequest request) {
        return adminService.updateUserStatus(id, request.status());
    }

    @GetMapping("/feature-flags")
    public List<FeatureFlagResponse> featureFlags() {
        return adminService.listFeatureFlags();
    }

    @PatchMapping("/feature-flags/{key}")
    public FeatureFlagResponse updateFeatureFlag(@PathVariable String key, @Valid @RequestBody UpdateFeatureFlagRequest request) {
        return adminService.updateFeatureFlag(key, request);
    }
}
