package com.interviewiq.interview.repository;

import com.interviewiq.interview.entity.InterviewTurn;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewTurnRepository extends JpaRepository<InterviewTurn, UUID> {

    List<InterviewTurn> findAllBySessionIdOrderBySequenceNoAsc(UUID sessionId);
}
