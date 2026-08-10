package com.interviewiq.user.dto;

import com.interviewiq.user.entity.Theme;
import jakarta.validation.constraints.Size;

/** PATCH semantics: a null field means "leave unchanged," not "clear this field." */
public record UpdatePreferencesRequest(
        Theme theme, @Size(max = 10) String language, Boolean emailNotifications, Boolean pushNotifications) {}
