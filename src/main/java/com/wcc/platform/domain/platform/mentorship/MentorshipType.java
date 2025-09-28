package com.wcc.platform.domain.platform.mentorship;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/* Type of mentorship cycle */
@Getter
@AllArgsConstructor
public enum MentorshipType {
  AD_HOC(1),
  LONG_TERM(2);

  private final int mentorshipTypeId;

  /** Get MentorshipType from its ID. */
  public static MentorshipType fromId(int typeId) {
    return Arrays.stream(MentorshipType.values())
        .filter(type -> type.mentorshipTypeId == typeId)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown MentorshipType id: " + typeId));
  }
}
