package com.wcc.platform.domain.exceptions;

/**
 * Exception thrown when a mentee attempts to submit a duplicate application.
 */
public class DuplicateApplicationException extends RuntimeException {

    public DuplicateApplicationException(final String message) {
        super(message);
    }

    public DuplicateApplicationException(
            final Long menteeId,
            final Long mentorId,
            final Long cycleId) {
        super(String.format(
            "Mentee %d has already applied to mentor %d for cycle %d",
            menteeId, mentorId, cycleId
        ));
    }
}
