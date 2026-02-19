package com.wcc.platform.domain.platform.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wcc.platform.domain.cms.PageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Program types and/or event category. */
@Getter
@AllArgsConstructor
public enum ProgramType {
  /** Community Clubs. */
  BOOK_CLUB("Book Club", false),
  WRITING_CLUB("Writing Club", false),
  CODING_CLUB("Coding Club Python", false),
  CAREER_CLUB("Career Club", false),
  SPEAKING_CLUB("Speaking Club", false),

  /** Tech Tracks */
  MACHINE_LEARNING("Machine Learning", false),
  CLOUD_TRACK("Cloud and DevOps", false),

  /** Interview Preparation */
  INTERVIEW_PREP("Interview Preparation", false),
  CV_CLINIC("CV Clinic", false),
  MOCK_INTERVIEW("Mock Interview", false),
  LEETCODE("Leetcode", false),

  MENTORSHIP("Mentorship", false),

  /** It is not a program, but an event category. */
  OTHERS("Others", true),
  EVENTS("Online and in-person Events", true),
  TECH_TALK("Tech Talk", true);

  private final String description;
  private final boolean eventTopicOnly;

  /** Find ProgramType by value. */
  @JsonCreator
  public static ProgramType findByValue(final String value) {
    for (final ProgramType type : values()) {
      if (type.description.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
        return type;
      }
    }

    return OTHERS;
  }

  @Override
  @JsonValue
  public String toString() {
    return description;
  }

  public String toPageId() {
    return PageType.ID_PREFIX + name();
  }
}
