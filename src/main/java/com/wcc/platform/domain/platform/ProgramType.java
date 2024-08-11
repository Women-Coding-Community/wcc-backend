package com.wcc.platform.domain.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Program types and also category */
@AllArgsConstructor
@Getter
public enum ProgramType {
  BOOK_CLUB(false),

  /** It is not a program, but an event category. */
  TECH_TALK(true),

  MENTORSHIP(false),
  WRITING_CLUB(false),
  OTHERS(true);

  private final boolean eventTopicOnly;
}
