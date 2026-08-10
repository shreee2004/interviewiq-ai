package com.interviewiq.user.dto;

import com.interviewiq.user.entity.ExperienceLevel;
import com.interviewiq.user.entity.UserProfile;
import java.time.Instant;
import java.util.UUID;

public record ProfileDto(
        String fullName,
        String headline,
        String avatarUrl,
        ExperienceLevel experienceLevel,
        String currentJobTitle,
        UUID targetJobRoleId,
        String githubUrl,
        String linkedinUrl,
        String portfolioUrl,
        String bio,
        boolean isPublic,
        Instant updatedAt) {

    public static ProfileDto from(UserProfile profile) {
        return new ProfileDto(
                profile.getFullName(),
                profile.getHeadline(),
                profile.getAvatarUrl(),
                profile.getExperienceLevel(),
                profile.getCurrentJobTitle(),
                profile.getTargetJobRoleId(),
                profile.getGithubUrl(),
                profile.getLinkedinUrl(),
                profile.getPortfolioUrl(),
                profile.getBio(),
                profile.isPublic(),
                profile.getUpdatedAt());
    }
}
