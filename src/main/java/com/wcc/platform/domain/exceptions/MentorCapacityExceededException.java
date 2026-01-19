package com.wcc.platform.domain.exceptions;

/**
 * Exception thrown when a mentor has reached their maximum capacity for a cycle.
 */
public class MentorCapacityExceededException extends RuntimeException {

    public MentorCapacityExceededException(final String message) {
        super(message);
    }

    public MentorCapacityExceededException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
