package com.wcc.platform.domain.exceptions;

/**
 * Exception thrown when a mentee application is not found.
 */
public class ApplicationNotFoundException extends RuntimeException {

    public ApplicationNotFoundException(final String message) {
        super(message);
    }

    public ApplicationNotFoundException(final Long applicationId) {
        super("Application not found with ID: " + applicationId);
    }
}
