package com.interviewiq.user.dto;

import com.interviewiq.user.entity.ExperienceLevel;
import com.interviewiq.user.entity.UserProfile;
import java.util.UUID;

/** Deliberately omits email, preferences, and any other non-public field. */
public record PublicProfileResponse(
        UUID userId,
        String fullName,
        String headline,
        String avatarUrl,
        ExperienceLevel experienceLevel,
        String currentJobTitle,
        String githubUrl,
        String linkedinUrl,
        String portfolioUrl,
        String bio) {

    public static PublicProfileResponse from(UserProfile profile) {
        return new PublicProfileResponse(
                profile.getUserId(),
                profile.getFullName(),
                profile.getHeadline(),
                profile.getAvatarUrl(),
                profile.getExperienceLevel(),
                profile.getCurrentJobTitle(),
                profile.getGithubUrl(),
                profile.getLinkedinUrl(),
                profile.getPortfolioUrl(),
                profile.getBio());
    }
}
