package com.wcc.platform.domain.platform.mentorship;

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
}
