package com.wcc.platform.domain.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Program types and/or event category. */
@AllArgsConstructor
@Getter
public enum ProgramType {
  BOOK_CLUB(false),
  MENTORSHIP(false),
  WRITING_CLUB(false),

  /** It is not a program, but an event category. */
  OTHERS(true),
  TECH_TALK(true);

  private final boolean eventTopicOnly;
}
