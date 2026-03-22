package com.wcc.platform.domain.exceptions;

/** Exception thrown when a mentee application is not dropped and cannot be change. */
public class ApplicationMenteeWorkflowException extends RuntimeException {

  public ApplicationMenteeWorkflowException(final String message) {
    super(message);
  }

  public ApplicationMenteeWorkflowException(final Long applicationId) {
    super("Application is not allowed to be changed ID: " + applicationId);
  }
}
