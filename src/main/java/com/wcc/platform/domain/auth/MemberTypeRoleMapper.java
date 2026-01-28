package com.wcc.platform.domain.auth;

import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.platform.type.RoleType;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MemberTypeRoleMapper {

  private static final Map<MemberType, RoleType> MEMBER_TYPE_TO_ROLE_MAP =
      Map.of(
          MemberType.DIRECTOR, RoleType.SUPER_ADMIN,
          MemberType.LEADER, RoleType.ADMIN,
          MemberType.MENTOR, RoleType.MENTOR,
          MemberType.MENTEE, RoleType.MENTEE,
          MemberType.COLLABORATOR, RoleType.CONTRIBUTOR,
          MemberType.EVANGELIST, RoleType.CONTRIBUTOR,
          MemberType.SPEAKER, RoleType.CONTRIBUTOR,
          MemberType.VOLUNTEER, RoleType.VIEWER,
          MemberType.MEMBER, RoleType.VIEWER,
          MemberType.PARTNER, RoleType.VIEWER);

  // Role hierarchy: higher number = more privileged
  private static final Map<RoleType, Integer> ROLE_HIERARCHY =
      Map.of(
          RoleType.SUPER_ADMIN, 100,
          RoleType.ADMIN, 80,
          RoleType.MENTOR, 60,
          RoleType.CONTRIBUTOR, 50,
          RoleType.MENTEE, 40,
          RoleType.VIEWER, 20);

  private MemberTypeRoleMapper() {
    // Utility class
  }

  /** Get role for a single MemberType. */
  public static RoleType getRoleForMemberType(MemberType memberType) {
    if (memberType == null) {
      throw new IllegalArgumentException("MemberType cannot be null");
    }
    return MEMBER_TYPE_TO_ROLE_MAP.getOrDefault(memberType, RoleType.VIEWER);
  }

  /**
   * Get all roles for a list of MemberTypes.
   *
   * @param memberTypes list of member types
   * @return set of all roles corresponding to the member types
   */
  public static Set<RoleType> getRolesForMemberTypes(List<MemberType> memberTypes) {
    if (memberTypes == null || memberTypes.isEmpty()) {
      return Set.of(RoleType.VIEWER);
    }

    return memberTypes.stream()
        .map(MemberTypeRoleMapper::getRoleForMemberType)
        .collect(Collectors.toSet());
  }

  /**
   * Get the highest privilege role from a list of MemberTypes. This determines the "primary" role
   * when a member has multiple types.
   *
   * @param memberTypes list of member types
   * @return the role with highest privilege level
   */
  public static RoleType getHighestRole(List<MemberType> memberTypes) {
    if (memberTypes == null || memberTypes.isEmpty()) {
      return RoleType.VIEWER;
    }

    return memberTypes.stream()
        .map(MemberTypeRoleMapper::getRoleForMemberType)
        .max(Comparator.comparing(role -> ROLE_HIERARCHY.getOrDefault(role, 0)))
        .orElse(RoleType.VIEWER);
  }

  /**
   * Get all permissions from multiple MemberTypes (union of all permissions).
   *
   * @param memberTypes list of member types
   * @return set of all unique permissions
   */
  public static Set<Permission> getAllPermissionsForMemberTypes(List<MemberType> memberTypes) {
    if (memberTypes == null || memberTypes.isEmpty()) {
      return RoleType.VIEWER.getPermissions();
    }

    return memberTypes.stream()
        .map(MemberTypeRoleMapper::getRoleForMemberType)
        .flatMap(role -> role.getPermissions().stream())
        .collect(Collectors.toSet());
  }

  /** Check if any of the member types maps to SUPER_ADMIN. */
  public static boolean isSuperAdmin(List<MemberType> memberTypes) {
    if (memberTypes == null) {
      return false;
    }
    return memberTypes.stream()
        .anyMatch(type -> getRoleForMemberType(type) == RoleType.SUPER_ADMIN);
  }

  /** Check if any of the member types maps to ADMIN or SUPER_ADMIN. */
  public static boolean isAdmin(List<MemberType> memberTypes) {
    if (memberTypes == null) {
      return false;
    }
    return memberTypes.stream()
        .map(MemberTypeRoleMapper::getRoleForMemberType)
        .anyMatch(role -> role == RoleType.SUPER_ADMIN || role == RoleType.ADMIN);
  }

  /** Check if member has a specific role through any of their member types. */
  public static boolean hasRole(List<MemberType> memberTypes, RoleType targetRole) {
    if (memberTypes == null || targetRole == null) {
      return false;
    }
    return memberTypes.stream()
        .map(MemberTypeRoleMapper::getRoleForMemberType)
        .anyMatch(role -> role == targetRole);
  }
}
