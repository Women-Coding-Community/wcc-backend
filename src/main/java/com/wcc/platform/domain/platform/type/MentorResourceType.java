package com.wcc.platform.domain.platform.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Mentor resource type. */
@Getter
@AllArgsConstructor
public enum MentorResourceType {
  BOOK_LIST(1),
  LINKS(2),
  ASSETS(3);

  private final int typeId;
}
