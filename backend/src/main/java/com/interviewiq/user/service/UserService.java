package com.interviewiq.user.service;

import com.interviewiq.auth.entity.User;
import com.interviewiq.auth.entity.UserStatus;
import com.interviewiq.auth.repository.UserRepository;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.user.dto.MeResponse;
import com.interviewiq.user.dto.PreferencesDto;
import com.interviewiq.user.dto.ProfileDto;
import com.interviewiq.user.dto.PublicProfileResponse;
import com.interviewiq.user.dto.UpdatePreferencesRequest;
import com.interviewiq.user.dto.UpdateProfileRequest;
import com.interviewiq.user.entity.UserPreferences;
import com.interviewiq.user.entity.UserProfile;
import com.interviewiq.user.repository.UserPreferencesRepository;
import com.interviewiq.user.repository.UserProfileRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reads {@link User} via the auth module's repository — entities are shared read models
 * across this modular monolith (docs/ARCHITECTURE.md §3's "no DTO leakage" rule is about
 * business logic, not persistence access), so {@code user} doesn't need its own copy of
 * core identity fields (email, role) just to assemble {@link MeResponse}.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final UserPreferencesRepository preferencesRepository;

    public UserService(
            UserRepository userRepository,
            UserProfileRepository profileRepository,
            UserPreferencesRepository preferencesRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.preferencesRepository = preferencesRepository;
    }

    @Transactional(readOnly = true)
    public MeResponse getMe(UUID userId) {
        User user = requireUser(userId);
        UserProfile profile = getOrCreateProfile(userId);
        UserPreferences preferences = getOrCreatePreferences(userId);
        return new MeResponse(
                user.getId(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getRole().name(),
                ProfileDto.from(profile),
                PreferencesDto.from(preferences));
    }

    public ProfileDto updateProfile(UUID userId, UpdateProfileRequest request) {
        UserProfile profile = getOrCreateProfile(userId);
        if (request.fullName() != null) profile.setFullName(request.fullName());
        if (request.headline() != null) profile.setHeadline(request.headline());
        if (request.avatarUrl() != null) profile.setAvatarUrl(request.avatarUrl());
        if (request.experienceLevel() != null) profile.setExperienceLevel(request.experienceLevel());
        if (request.currentJobTitle() != null) profile.setCurrentJobTitle(request.currentJobTitle());
        if (request.targetJobRoleId() != null) profile.setTargetJobRoleId(request.targetJobRoleId());
        if (request.githubUrl() != null) profile.setGithubUrl(request.githubUrl());
        if (request.linkedinUrl() != null) profile.setLinkedinUrl(request.linkedinUrl());
        if (request.portfolioUrl() != null) profile.setPortfolioUrl(request.portfolioUrl());
        if (request.bio() != null) profile.setBio(request.bio());
        if (request.isPublic() != null) profile.setPublic(request.isPublic());
        return ProfileDto.from(profileRepository.save(profile));
    }

    public PreferencesDto updatePreferences(UUID userId, UpdatePreferencesRequest request) {
        UserPreferences preferences = getOrCreatePreferences(userId);
        if (request.theme() != null) preferences.setTheme(request.theme());
        if (request.language() != null) preferences.setLanguage(request.language());
        if (request.emailNotifications() != null) preferences.setEmailNotifications(request.emailNotifications());
        if (request.pushNotifications() != null) preferences.setPushNotifications(request.pushNotifications());
        return PreferencesDto.from(preferencesRepository.save(preferences));
    }

    @Transactional(readOnly = true)
    public PublicProfileResponse getPublicProfile(UUID userId) {
        UserProfile profile = profileRepository.findById(userId)
                .filter(UserProfile::isPublic)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
        return PublicProfileResponse.from(profile);
    }

    public void deleteMe(UUID userId) {
        User user = requireUser(userId);
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    private User requireUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> ResourceNotFoundException.of("User", userId));
    }

    private UserProfile getOrCreateProfile(UUID userId) {
        return profileRepository.findById(userId)
                .orElseGet(() -> UserProfile.builder().userId(userId).build());
    }

    private UserPreferences getOrCreatePreferences(UUID userId) {
        return preferencesRepository.findById(userId)
                .orElseGet(() -> UserPreferences.builder().userId(userId).build());
    }
}
