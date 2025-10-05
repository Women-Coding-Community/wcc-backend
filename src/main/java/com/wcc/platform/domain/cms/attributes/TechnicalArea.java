package com.wcc.platform.domain.cms.attributes;

import lombok.AllArgsConstructor;
import lombok.Getter;

/* Technical Areas enum list. */
@Getter
@AllArgsConstructor
public enum TechnicalArea {
  BACKEND(1),
  FRONTEND(4),
  FULLSTACK(5),
  DEVOPS(3),
  DISTRIBUTED_SYSTEMS(9),
  DATA_SCIENCE(2),
  DATA_ENGINEERING(10),
  MACHINE_LEARNING(15),
  MOBILE_ANDROID(6),
  MOBILE_IOS(11),
  QA(8),
  BUSINESS_ANALYSIS(12),
  PRODUCT_MANAGEMENT(13),
  PROJECT_MANAGEMENT(14),
  ENGINEERING_MANAGEMENT(16),
  OTHER(7);

  private final int technicalAreaId;
}
