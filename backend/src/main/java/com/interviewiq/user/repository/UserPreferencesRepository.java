package com.interviewiq.user.repository;

import com.interviewiq.user.entity.UserPreferences;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID> {}
