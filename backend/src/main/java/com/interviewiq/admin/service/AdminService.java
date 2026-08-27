package com.interviewiq.admin.service;

import com.interviewiq.admin.dto.AdminUserResponse;
import com.interviewiq.admin.dto.FeatureFlagResponse;
import com.interviewiq.admin.dto.UpdateFeatureFlagRequest;
import com.interviewiq.admin.entity.FeatureFlag;
import com.interviewiq.admin.repository.FeatureFlagRepository;
import com.interviewiq.auth.entity.User;
import com.interviewiq.auth.entity.UserStatus;
import com.interviewiq.auth.repository.UserRepository;
import com.interviewiq.common.dto.PageResponse;
import com.interviewiq.common.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final FeatureFlagRepository featureFlagRepository;

    public AdminService(UserRepository userRepository, FeatureFlagRepository featureFlagRepository) {
        this.userRepository = userRepository;
        this.featureFlagRepository = featureFlagRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponse> listUsers(UserStatus status, String email, Pageable pageable) {
        String emailPattern = email == null ? null : "%" + email.toLowerCase() + "%";
        return PageResponse.from(userRepository.search(status, emailPattern, pageable).map(AdminUserResponse::from));
    }

    public AdminUserResponse updateUserStatus(UUID userId, UserStatus status) {
        User user = userRepository.findById(userId).orElseThrow(() -> ResourceNotFoundException.of("User", userId));
        user.setStatus(status);
        return AdminUserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public List<FeatureFlagResponse> listFeatureFlags() {
        return featureFlagRepository.findAll().stream().map(FeatureFlagResponse::from).toList();
    }

    public FeatureFlagResponse updateFeatureFlag(String key, UpdateFeatureFlagRequest request) {
        FeatureFlag flag = featureFlagRepository.findById(key).orElseGet(() -> FeatureFlag.builder().key(key).build());
        if (request.enabled() != null) {
            flag.setEnabled(request.enabled());
        }
        if (request.description() != null) {
            flag.setDescription(request.description());
        }
        return FeatureFlagResponse.from(featureFlagRepository.save(flag));
    }
}
