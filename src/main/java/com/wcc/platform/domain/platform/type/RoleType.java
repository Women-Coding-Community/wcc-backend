package com.wcc.platform.domain.platform.type;

import com.wcc.platform.domain.auth.Permission;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** All available roles into administration platform. */
@Getter
@AllArgsConstructor
public enum RoleType {
  ADMIN(1, "Platform Administrator", Set.of(Permission.values())),
  MENTORSHIP_ADMIN(
      20,
      "Mentorship Administrator",
      Set.of(
          Permission.USER_READ,
          Permission.MEMBER_READ,
          Permission.MEMBER_WRITE,
          Permission.MENTOR_APPROVE,
          Permission.MENTEE_APPROVE,
          Permission.CYCLE_EMAIL_SEND,
          Permission.MATCH_MANAGE,
          Permission.MENTOR_APPL_READ,
          Permission.MENTOR_APPL_WRITE,
          Permission.MENTOR_PROFILE_UPDATE,
          Permission.EMAIL_TEMPLATE_MANAGE,
          Permission.SEND_EMAIL)),
  LEADER(
      4,
      "Platform Leader",
      Set.of(
          Permission.USER_READ,
          Permission.USER_WRITE,
          Permission.MEMBER_READ,
          Permission.MEMBER_WRITE,
          Permission.EMAIL_TEMPLATE_MANAGE)),
  MENTEE(
      5, "Mentee In Community", Set.of(Permission.MENTEE_APPL_SUBMIT, Permission.MENTEE_APPL_READ)),
  MENTOR(
      6,
      "Mentor In Community",
      Set.of(
          Permission.MENTOR_APPL_READ,
          Permission.MENTOR_APPL_WRITE,
          Permission.MENTOR_PROFILE_UPDATE)),

  CONTRIBUTOR(
      2,
      "Contributor In Community",
      Set.of(Permission.MEMBER_READ, Permission.EMAIL_TEMPLATE_MANAGE)),
  VIEWER(7, "Member In Community", Set.of(Permission.MEMBER_READ));

  private final int typeId;
  private final String description;
  private final Set<Permission> permissions;

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
    return VIEWER;
  }

  @Override
  public String toString() {
    return description;
  }
}
