package com.interviewiq.admin.dto;

import com.interviewiq.admin.entity.FeatureFlag;
import java.time.Instant;

public record FeatureFlagResponse(String key, boolean enabled, String description, Instant updatedAt) {

    public static FeatureFlagResponse from(FeatureFlag flag) {
        return new FeatureFlagResponse(flag.getKey(), flag.isEnabled(), flag.getDescription(), flag.getUpdatedAt());
    }
}
