package com.wcc.platform.domain.platform.mentorship;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/* Type of mentorship cycle */
@Getter
@AllArgsConstructor
public enum MentorshipType {
  AD_HOC(1),
  LONG_TERM(2),
  STUDY_GROUP(3);

  private final int mentorshipTypeId;

  public static MentorshipType fromId(int id) {
    Arrays.stream(MentorshipType.values())
        .filter(type -> type.mentorshipTypeId == id)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown MentorshipType id: " + id));
    return null;
  }
}
