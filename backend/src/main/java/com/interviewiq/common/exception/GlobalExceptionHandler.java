package com.interviewiq.common.exception;

import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Single point of exception -> HTTP mapping for every controller, producing the
 * RFC 7807 problem-detail shape documented in docs/API_DESIGN.md §1. Controllers
 * should never catch these exceptions themselves — throw and let this advice map
 * them consistently.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        return problemDetail(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleException ex, WebRequest request) {
        return problemDetail(HttpStatus.UNPROCESSABLE_CONTENT, "BUSINESS_RULE_VIOLATION", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return problemDetail(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", detail, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex, WebRequest request) {
        return problemDetail(HttpStatus.UNAUTHORIZED, "UNAUTHENTICATED", "Authentication required", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return problemDetail(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You do not have access to this resource", request);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, WebRequest request) {
        return problemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred", request);
    }

    private ProblemDetail problemDetail(HttpStatus status, String type, String detail, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(java.net.URI.create("urn:interviewiq:" + type));
        problemDetail.setProperty("traceId", UUID.randomUUID().toString());
        problemDetail.setInstance(java.net.URI.create(request.getDescription(false).replace("uri=", "")));
        return problemDetail;
    }
}
