package com.wcc.platform.domain.platform.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** All available roles into administration platform. */
@Getter
@AllArgsConstructor
public enum RoleType {
  ADMIN(1, "Platform Administrator"),
  MEMBER(2, "Community Member"),

  MENTORSHIP_ADMIN(20, "Mentorship Administrator"),
  MENTORSHIP_EDITOR(21, "Mentorship Team"),

  MAIL_ADMIN(30, "Newsletter Administrator"),
  MAIL_EDITOR(31, "Newsletter Editor"),
  MAIL_PUBLISHER(33, "Newsletter Publisher"),
  MAIL_SUBSCRIBER(32, "Newsletter Subscriber Coordinator"),
  MAIL_VIEWER(34, "Newsletter Viewer"),

  CONTENT_ADMIN(40, "Website Content Administrator"),
  CONTENT_EDITOR(41, "Website Content Editor"),
  CONTENT_VIEWER(42, "Website Content Viewer");

  private final int typeId;
  private final String description;

  /**
   * Retrieves the corresponding {@code MemberType} enum value based on a given type ID. If no match
   * is found, the default {@code MEMBER} type is returned.
   *
   * @param typeId the integer ID representing a specific {@code MemberType}
   * @return the {@code MemberType} that matches the given ID, or {@code MEMBER} if no match is
   *     found
   */
  public static RoleType fromId(final int typeId) {
    for (final RoleType type : values()) {
      if (type.getTypeId() == typeId) {
        return type;
      }
    }
    return MEMBER;
  }

  @Override
  public String toString() {
    return description;
  }
}
