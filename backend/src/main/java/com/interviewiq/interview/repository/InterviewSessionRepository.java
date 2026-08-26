package com.interviewiq.interview.repository;

import com.interviewiq.interview.entity.InterviewSession;
import com.interviewiq.interview.entity.SessionStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {

    Optional<InterviewSession> findByIdAndUserId(UUID id, UUID userId);

    Page<InterviewSession> findAllByUserId(UUID userId, Pageable pageable);

    Page<InterviewSession> findAllByUserIdAndStatus(UUID userId, SessionStatus status, Pageable pageable);
}
