package com.interviewiq.interview.dto;

import com.interviewiq.interview.entity.JobRole;
import java.util.UUID;

public record JobRoleResponse(UUID id, String slug, String name, String category) {

    public static JobRoleResponse from(JobRole jobRole) {
        return new JobRoleResponse(jobRole.getId(), jobRole.getSlug(), jobRole.getName(), jobRole.getCategory());
    }
}
