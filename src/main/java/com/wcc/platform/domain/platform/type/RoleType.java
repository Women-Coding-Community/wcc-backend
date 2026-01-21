package com.wcc.platform.domain.platform.type;

import com.wcc.platform.domain.auth.Permission;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** All available roles into administration platform. */
@Getter
@AllArgsConstructor
public enum RoleType {
  SUPER_ADMIN(1, "Platform Super Administrator", Set.of(Permission.values())),
  ADMIN(
      4,
      "Platform Administrator",
      Set.of(
          Permission.USER_READ,
          Permission.MENTOR_APPROVE,
          Permission.MENTEE_APPROVE,
          Permission.CYCLE_EMAIL_SEND,
          Permission.MATCH_MANAGE,
          Permission.MENTOR_APPLICATION_READ)),
  MENTEE(
      5,
      "Mentee In Community",
      Set.of(Permission.MENTEE_APPLICATION_SUBMIT, Permission.MENTEE_APPLICATION_READ)),
  MENTOR(
      6,
      "Mentor In Community",
      Set.of(
          Permission.MENTOR_APPLICATION_READ,
          Permission.MENTOR_APPLICATION_WRITE,
          Permission.MENTOR_PROFILE_UPDATE)),

  CONTRIBUTOR(2, "Contributor In Community", Set.of(Permission.USER_READ)),
  VIEWER(7, "Member In Community", Set.of(Permission.USER_READ));

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

  /** Check if this role has a specific permission. */
  public boolean hasPermission(Permission permission) {
    return permissions.contains(permission);
  }

  /** Check if this role has any of the specified permissions. */
  public boolean hasAnyPermission(Permission... requiredPermissions) {
    for (Permission permission : requiredPermissions) {
      if (permissions.contains(permission)) {
        return true;
      }
    }
    return false;
  }

  /** Check if this role has all of the specified permissions. */
  public boolean hasAllPermissions(Permission... requiredPermissions) {
    for (Permission permission : requiredPermissions) {
      if (!permissions.contains(permission)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return description;
  }
}
