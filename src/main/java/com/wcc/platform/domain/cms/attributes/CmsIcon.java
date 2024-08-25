package com.wcc.platform.domain.cms.attributes;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Available cms icons to be used by programs and internal pages. */
@Getter
@AllArgsConstructor
public enum CmsIcon {
  BOOK("book_2"),
  CALENDAR("calendar_month"),
  CODE("code_blocks"),
  DIVERSITY("diversity_2"),
  GROUP("group"),
  WORK("work");

  private final String iconName;

  @Override
  @JsonValue
  public String toString() {
    return iconName;
  }
}
