package com.wcc.platform.domain.platform;

public enum ProgramType {
  BOOK_CLUB,
  TECH_TALK,

  MENTORSHIP,
  WRITING_CLUB,
  OTHERS;

  @Override
  public String toString() {
    return name();
  }
}
