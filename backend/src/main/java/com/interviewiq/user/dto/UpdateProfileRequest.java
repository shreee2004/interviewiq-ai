package com.interviewiq.user.dto;

import com.interviewiq.user.entity.ExperienceLevel;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/** PATCH semantics: a null field means "leave unchanged," not "clear this field." */
public record UpdateProfileRequest(
        @Size(max = 255) String fullName,
        @Size(max = 255) String headline,
        @Size(max = 500) String avatarUrl,
        ExperienceLevel experienceLevel,
        @Size(max = 255) String currentJobTitle,
        UUID targetJobRoleId,
        @Size(max = 500) String githubUrl,
        @Size(max = 500) String linkedinUrl,
        @Size(max = 500) String portfolioUrl,
        String bio,
        Boolean isPublic) {}
