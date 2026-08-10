package com.interviewiq.user.dto;

import java.util.UUID;

public record MeResponse(UUID id, String email, boolean emailVerified, String role, ProfileDto profile, PreferencesDto preferences) {}
