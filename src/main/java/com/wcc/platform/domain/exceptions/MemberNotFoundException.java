package com.wcc.platform.domain.exceptions;

/** Platform MemberNotFoundException exception. */
public class MemberNotFoundException extends RuntimeException {
  public MemberNotFoundException(String email) {
    super("Member not found: " + email);
  }
}
