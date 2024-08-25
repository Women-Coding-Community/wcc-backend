package com.wcc.platform.domain.platform;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Program types and/or event category. */
@Getter
@AllArgsConstructor
public enum ProgramType {
  BOOK_CLUB("Book Club", false),
  MENTORSHIP("Mentorship", false),
  WRITING_CLUB("Writing Club", false),

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
}
