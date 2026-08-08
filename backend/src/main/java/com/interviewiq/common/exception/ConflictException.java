package com.interviewiq.common.exception;

/** Thrown when a request conflicts with existing state (e.g. registering an email already in use). */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
