package com.interviewiq.common.exception;

/** Thrown when an uploaded file fails a basic format/content-type check. */
public class InvalidFileException extends RuntimeException {

    public InvalidFileException(String message) {
        super(message);
    }
}
