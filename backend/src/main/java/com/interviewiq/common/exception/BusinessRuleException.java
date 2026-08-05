package com.interviewiq.common.exception;

/**
 * Thrown when a request is well-formed but violates a domain rule
 * (e.g. submitting an answer to an already-completed interview session).
 * Maps to HTTP 422 — distinct from bean-validation failures (400).
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
