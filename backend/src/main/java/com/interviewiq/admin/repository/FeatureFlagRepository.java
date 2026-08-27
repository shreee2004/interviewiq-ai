package com.interviewiq.admin.repository;

import com.interviewiq.admin.entity.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, String> {}
