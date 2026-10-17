package com.wcc.platform.domain.platform.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Types of feedback. */
@Getter
@AllArgsConstructor
public enum FeedbackType {
  MENTOR_REVIEW(1, "Review of a mentor by a mentee"),
  COMMUNITY_GENERAL(2, "General feedback about the community"),
  MENTORSHIP_PROGRAM(3, "Feedback about the mentorship program");

  private final int typeId;
  private final String description;

  /**
   * Retrieves the corresponding {@code FeedbackType} enum value based on a given type ID. If no
   * match is found, the default {@code COMMUNITY_GENERAL} type is returned.
   *
   * @param typeId the integer ID representing a specific {@code FeedbackType}
   * @return the {@code FeedbackType} that matches the given ID, or {@code COMMUNITY_GENERAL} if no
   *     match is found
   */
  public static FeedbackType fromId(final int typeId) {
    for (final FeedbackType type : values()) {
      if (type.getTypeId() == typeId) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown FeedbackType id: " + typeId);
  }
}
