package com.wcc.platform.domain.platform;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.wcc.platform.domain.cms.PageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Program types and/or event category. */
@Getter
@AllArgsConstructor
public enum ProgramType {
  BOOK_CLUB("Book Club", false),
  CODING_CLUB("Coding Club Python", false),
  CAREER_CLUB("Career Club", false),
  SPEAKING_CLUB("Speaking Club", false),
  WRITING_CLUB("Writing Club", false),
  CLOUD("Cloud and DevOps", false),
  MACHINE_LEARNING("Machine Learning", false),
  CV_CLINIC("CV Clinic", false),
  MOCK_INTERVIEW("Mock Interview", false),
  LEETCODE("Leetcode", false),

  /** It is not a program, but an event category. */
  OTHERS("Others", true),
  TECH_TALK("Tech Talk", true);

  private final String description;
  private final boolean eventTopicOnly;

  /** Find program type by value string. */
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
