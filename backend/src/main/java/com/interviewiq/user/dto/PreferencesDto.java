package com.interviewiq.user.dto;

import com.interviewiq.user.entity.Theme;
import com.interviewiq.user.entity.UserPreferences;

public record PreferencesDto(Theme theme, String language, boolean emailNotifications, boolean pushNotifications) {

    public static PreferencesDto from(UserPreferences preferences) {
        return new PreferencesDto(
                preferences.getTheme(),
                preferences.getLanguage(),
                preferences.isEmailNotifications(),
                preferences.isPushNotifications());
    }
}
