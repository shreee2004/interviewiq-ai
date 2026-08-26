package com.interviewiq.interview.repository;

import com.interviewiq.interview.entity.JobRole;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRoleRepository extends JpaRepository<JobRole, UUID> {}
