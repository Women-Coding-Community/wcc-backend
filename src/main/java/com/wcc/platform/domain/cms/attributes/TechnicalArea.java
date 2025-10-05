package com.wcc.platform.domain.cms.attributes;

import lombok.AllArgsConstructor;
import lombok.Getter;

/* Technical Areas enum list. */
@Getter
@AllArgsConstructor
public enum TechnicalArea {
  BACKEND(1),
  DATA_SCIENCE(2),
  DEVOPS(3),
  FRONTEND(4),
  FULLSTACK(5),
  MOBILE(6),
  OTHER(7),
  QA(8);

  private final int technicalAreaId;
}
