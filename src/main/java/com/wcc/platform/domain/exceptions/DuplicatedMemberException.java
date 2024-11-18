package com.wcc.platform.domain.exceptions;

/** File/Database repository duplicated member exception. */
public class DuplicatedMemberException extends RuntimeException {
  public DuplicatedMemberException(final String message) {
    super(message);
  }
}
