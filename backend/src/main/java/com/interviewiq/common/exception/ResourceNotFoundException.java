package com.interviewiq.common.exception;

/** Thrown when a requested entity does not exist or is not visible to the caller. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resourceName, Object identifier) {
        return new ResourceNotFoundException(resourceName + " not found: " + identifier);
    }
}
