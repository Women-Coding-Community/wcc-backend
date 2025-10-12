package com.wcc.platform.domain.platform.mentorship;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Defines the different types of mentorship available. This enumeration categorizes mentorships
 * based on their duration or structure. <br>
 * Types: - AD_HOC: Represents a short-term or on-demand mentorship arrangement. <br>
 * - LONG_TERM: Represents an extended mentorship relationship over a longer duration.
 */
@Getter
@AllArgsConstructor
public enum MentorshipType {
  AD_HOC(1),
  LONG_TERM(2);

  private final int mentorshipTypeId;

  /** Get MentorshipType from its ID. */
  public static MentorshipType fromId(final int typeId) {
    return Arrays.stream(values())
        .filter(type -> type.mentorshipTypeId == typeId)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown MentorshipType id: " + typeId));
  }
}
