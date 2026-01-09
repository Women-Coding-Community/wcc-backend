package com.wcc.platform.domain.exceptions;

/** Exception thrown when an email fails to send. */
public class EmailSendException extends RuntimeException {

  public EmailSendException(final String message) {
    super(message);
  }

  public EmailSendException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
