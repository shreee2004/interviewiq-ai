package com.interviewiq.interview.dto;

import com.interviewiq.interview.entity.AnswerMode;
import com.interviewiq.interview.entity.InterviewTurn;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TurnResponse(
        UUID id,
        int sequenceNo,
        String questionText,
        String questionTopic,
        String answerText,
        AnswerMode answerMode,
        Integer responseTimeMs,
        Integer fillerWordCount,
        BigDecimal speakingWpm,
        Instant askedAt,
        Instant answeredAt) {

    public static TurnResponse from(InterviewTurn turn) {
        return new TurnResponse(
                turn.getId(),
                turn.getSequenceNo(),
                turn.getQuestionText(),
                turn.getQuestionTopic(),
                turn.getAnswerText(),
                turn.getAnswerMode(),
                turn.getResponseTimeMs(),
                turn.getFillerWordCount(),
                turn.getSpeakingWpm(),
                turn.getAskedAt(),
                turn.getAnsweredAt());
    }
}
