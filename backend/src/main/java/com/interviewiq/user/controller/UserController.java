package com.interviewiq.user.controller;

import com.interviewiq.user.dto.MeResponse;
import com.interviewiq.user.dto.PreferencesDto;
import com.interviewiq.user.dto.ProfileDto;
import com.interviewiq.user.dto.PublicProfileResponse;
import com.interviewiq.user.dto.UpdatePreferencesRequest;
import com.interviewiq.user.dto.UpdateProfileRequest;
import com.interviewiq.user.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** See docs/API_DESIGN.md §3. */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal String userId) {
        return userService.getMe(UUID.fromString(userId));
    }

    @PatchMapping("/me/profile")
    public ProfileDto updateProfile(@AuthenticationPrincipal String userId, @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(UUID.fromString(userId), request);
    }

    @PatchMapping("/me/preferences")
    public PreferencesDto updatePreferences(
            @AuthenticationPrincipal String userId, @Valid @RequestBody UpdatePreferencesRequest request) {
        return userService.updatePreferences(UUID.fromString(userId), request);
    }

    @GetMapping("/{id}/public-profile")
    public PublicProfileResponse publicProfile(@PathVariable UUID id) {
        return userService.getPublicProfile(id);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal String userId) {
        userService.deleteMe(UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }
}
