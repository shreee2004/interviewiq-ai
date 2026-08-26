package com.interviewiq.interview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * One question/answer exchange within an {@link InterviewSession} (docs/DATABASE.md §2.5).
 * Populated by the live WebSocket Q&A gateway — a future slice once the {@code ai} module
 * (Phase 4) exists to actually generate questions and evaluate answers. Built now purely
 * so {@code GET /interviews/{id}} can return a (currently always empty) transcript.
 */
@Entity
@Table(name = "interview_turns")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewTurn {

    @Id
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "sequence_no", nullable = false)
    private int sequenceNo;

    @Column(name = "question_text", nullable = false)
    private String questionText;

    @Column(name = "question_topic")
    private String questionTopic;

    @Column(name = "answer_text")
    private String answerText;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_mode", length = 10)
    private AnswerMode answerMode;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Column(name = "filler_word_count")
    private Integer fillerWordCount;

    @Column(name = "speaking_wpm")
    private BigDecimal speakingWpm;

    @CreatedDate
    @Column(name = "asked_at", nullable = false, updatable = false)
    private Instant askedAt;

    @Column(name = "answered_at")
    private Instant answeredAt;
}
