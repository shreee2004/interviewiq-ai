package com.interviewiq.analytics.repository;

import com.interviewiq.analytics.entity.UserStreak;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStreakRepository extends JpaRepository<UserStreak, UUID> {}
