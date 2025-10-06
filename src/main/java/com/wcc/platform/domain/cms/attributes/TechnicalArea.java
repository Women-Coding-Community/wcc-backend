package com.wcc.platform.domain.cms.attributes;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents various technical areas within of mentor or a member of the community.
 *
 * <p>Each enum constant corresponds to a specific technical domain and has an associated unique
 * identifier that can be used for categorization and filtering.
 */
@Getter
@AllArgsConstructor
public enum TechnicalArea {
  BACKEND(1),
  BUSINESS_ANALYSIS(12),
  CLOUD_ENGINEER(17),
  DATA_SCIENCE(2),
  DATA_ENGINEERING(10),
  DEVOPS(3),
  DISTRIBUTED_SYSTEMS(9),
  ENG_MANAGEMENT(16),
  FRONTEND(4),
  FULLSTACK(5),
  MACHINE_LEARNING(15),
  MOBILE_ANDROID(6),
  MOBILE_IOS(11),
  OTHER(7),
  PROD_MANAGEMENT(13),
  PROJ_MANAGEMENT(14),
  QA(8);

  private final int technicalAreaId;
}
