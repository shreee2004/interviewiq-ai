package com.interviewiq.common.exception;

import java.net.URI;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

/**
 * Builds the RFC 7807 problem-detail shape documented in docs/API_DESIGN.md §1. Shared by
 * {@link GlobalExceptionHandler} (exceptions thrown inside a controller) and the Spring
 * Security entry point/access-denied handler in the config package (exceptions thrown by
 * the filter chain, before a controller is ever reached — outside what
 * {@code @RestControllerAdvice} can intercept), so both paths produce an identical body.
 */
public final class ProblemDetailFactory {

    private ProblemDetailFactory() {}

    public static ProblemDetail create(HttpStatus status, String type, String detail, String instancePath) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create("urn:interviewiq:" + type));
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        problemDetail.setInstance(URI.create(instancePath));
        return problemDetail;
    }
}
