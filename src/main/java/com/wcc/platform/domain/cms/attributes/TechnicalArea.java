package com.wcc.platform.domain.cms.attributes;

import java.util.List;
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
  BACKEND(1, "Backend"),
  BUSINESS_ANALYSIS(12, "Business Analysis"),
  CLOUD_ENGINEER(17, "Cloud Engineer"),
  DATA_SCIENCE(2, "Data Science"),
  DATA_ENGINEERING(10, "Data Engineering"),
  DEVOPS(3, "DevOps"),
  DISTRIBUTED_SYSTEMS(9, "Distributed Systems"),
  ENG_MANAGEMENT(16, "Engineering Management"),
  FRONTEND(4, "Frontend"),
  FULLSTACK(5, "Fullstack"),
  MACHINE_LEARNING(15, "Machine Learning"),
  MOBILE_ANDROID(6, "Mobile Android"),
  MOBILE_IOS(11, "Mobile iOS"),
  OTHER(7, "Other"),
  PROD_MANAGEMENT(13, "Product Management"),
  PROJ_MANAGEMENT(14, "Project Management"),
  QA(8, "Quality Assurance");

  private final int technicalAreaId;
  private final String description;

  /** Find the technical area by id. */
  public static TechnicalArea fromId(final Integer id) {
    for (final TechnicalArea focus : values()) {
      if (focus.technicalAreaId == id) {
        return focus;
      }
    }
    return OTHER;
  }

  public static List<TechnicalArea> getAll() {
    return List.of(values());
  }

  @Override
  public String toString() {
    return description;
  }
}
