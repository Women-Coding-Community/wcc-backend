package com.wcc.platform.domain.cms.attributes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CmsIcon {
  ICON_1("classIcon1"),
  ICON_2("classIcon2"),
  ICON_3("classIcon3"),
  DIVERSITY_2("Diversity2");

  private final String className;

  @Override
  public String toString() {
    return className;
  }
}
