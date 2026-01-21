package com.wcc.platform.domain.auth;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.RoleType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain object representing an application user linked to a Member. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount {
  private Integer id;
  private Long memberId;
  private String email;
  private String passwordHash;
  private List<RoleType> roles;
  private boolean enabled;

  /** Get all permissions from all assigned roles in UserAccount. */
  public Set<Permission> getPermissions() {
    if (roles == null || roles.isEmpty()) {
      return Set.of();
    }

    return roles.stream()
        .flatMap(role -> role.getPermissions().stream())
        .collect(Collectors.toSet());
  }

  /* */
  /** Check if user has a specific permission in assigned roles. */
  /*
  public boolean hasPermission(Permission permission) {
    return getPermissions().contains(permission);
  }

  */
  /** Check if user has any of the specified permissions. */
  /*
  public boolean hasAnyPermission(Permission... permissions) {
    Set<Permission> userPermissions = getPermissions();
    return Arrays.stream(permissions).anyMatch(userPermissions::contains);
  }

  */
  /** Check if user has all of the specified permissions. */
  /*
  public boolean hasAllPermissions(Permission... permissions) {
    Set<Permission> userPermissions = getPermissions();
    return Arrays.stream(permissions).allMatch(userPermissions::contains);
  }

  */
  /** Check if user has a specific role. */
  /*
  public boolean hasRole(RoleType role) {
    return roles != null && roles.contains(role);
  }

  */
  /** Check if user has any of the specified roles. */
  /*
  public boolean hasAnyRole(RoleType... rolesToCheck) {
    if (roles == null) {
      return false;
    }
    return Arrays.stream(rolesToCheck).anyMatch(roles::contains);
  }*/

  /**
   * A record that encapsulates a User within the platform.
   *
   * <p>This record represents the relationship between a {@link UserAccount} and a {@link Member}.
   * A User consists of a user account for authentication purposes and a member entity containing
   * personal and community-related details.
   */
  public record User(UserAccount userAccount, Member member) {

    /**
     * Get the primary role based on member types. Returns the highest privilege role when member
     * has multiple types.
     *
     * <p>For example, if a member is both MENTEE and MENTOR, their primary role will be MENTOR
     * (since MENTOR has higher privileges).
     */
    public RoleType getPrimaryRole() {
      if (member == null || member.getMemberTypes() == null || member.getMemberTypes().isEmpty()) {
        return RoleType.VIEWER;
      }
      return MemberTypeRoleMapper.getHighestRole(member.getMemberTypes());
    }

    /**
     * Get all roles derived from member types. A member can have multiple roles if they have
     * multiple member types.
     *
     * <p>For example, a member who is both MENTOR and COLLABORATOR will have both MENTOR_ROLE and
     * CONTRIBUTOR roles.
     */
    public Set<RoleType> getAllMemberRoles() {
      if (member == null || member.getMemberTypes() == null) {
        return Set.of(RoleType.VIEWER);
      }
      return MemberTypeRoleMapper.getRolesForMemberTypes(member.getMemberTypes());
    }

    /**
     * Get all roles including both: 1. Roles derived from member types 2. Roles explicitly assigned
     * in UserAccount
     */
    public Set<RoleType> getAllRoles() {
      Set<RoleType> allRoles = new HashSet<>(getAllMemberRoles());
      if (userAccount.getRoles() != null) {
        allRoles.addAll(userAccount.getRoles());
      }
      return allRoles;
    }

    /**
     * Get all permissions including those from: 1. All member types (union of permissions) 2.
     * Explicitly assigned roles in UserAccount
     */
    public Set<Permission> getAllPermissions() {
      Set<Permission> permissions = new HashSet<>();

      // Add permissions from member types
      if (member != null && member.getMemberTypes() != null) {
        permissions.addAll(
            MemberTypeRoleMapper.getAllPermissionsForMemberTypes(member.getMemberTypes()));
      }

      // Add permissions from explicitly assigned roles
      permissions.addAll(userAccount.getPermissions());

      return permissions;
    }

    /**
     * Check if user has a specific permission. Checks permissions from both member types and
     * assigned roles.
     */
    public boolean hasPermission(Permission permission) {
      return getAllPermissions().contains(permission);
    }

    /**
     * Check if user has a specific role. Checks both member-type-derived roles and explicitly
     * assigned roles.
     */
    public boolean hasRole(RoleType role) {
      return getAllRoles().contains(role);
    }

    /** Check if user has any of the specified roles. */
    public boolean hasAnyRole(RoleType... roles) {
      Set<RoleType> userRoles = getAllRoles();
      return Arrays.stream(roles).anyMatch(userRoles::contains);
    }

    /** Check if user is a super admin (has DIRECTOR member type). */
    public boolean isSuperAdmin() {
      if (member == null || member.getMemberTypes() == null) {
        return false;
      }
      return MemberTypeRoleMapper.isSuperAdmin(member.getMemberTypes());
    }

    /** Check if user is an admin (has DIRECTOR or LEADER member type). */
    public boolean isAdmin() {
      if (member == null || member.getMemberTypes() == null) {
        return false;
      }
      return MemberTypeRoleMapper.isAdmin(member.getMemberTypes());
    }
  }
}
