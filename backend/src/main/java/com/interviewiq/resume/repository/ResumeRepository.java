package com.interviewiq.resume.repository;

import com.interviewiq.resume.entity.Resume;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {

    List<Resume> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Resume> findByIdAndUserId(UUID id, UUID userId);

    @Modifying
    @Query("update Resume r set r.active = false where r.userId = :userId and r.active = true")
    void deactivateAllForUser(@Param("userId") UUID userId);
}
