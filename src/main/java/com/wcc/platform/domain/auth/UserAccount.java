package com.wcc.platform.domain.auth;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.RoleType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.CollectionUtils;

/** Domain object representing an application user linked to a Member. */
@Data
@AllArgsConstructor
public class UserAccount {
  private Integer id;
  private Long memberId;
  private String email;
  private String passwordHash;
  private List<RoleType> roles;
  private boolean enabled;

  public UserAccount(final Long memberId, final String email, RoleType... roles) {
    this.memberId = memberId;
    this.email = email;
    this.passwordHash = PasswordGenerator.generateRandomPassword();
    this.roles = Arrays.asList(roles);
    this.enabled = true;
  }

  /** Get all permissions from all assigned roles in UserAccount. */
  public Set<Permission> getPermissions() {
    if (roles == null || CollectionUtils.isEmpty(roles)) {
      return Set.of();
    }

    return roles.stream()
        .flatMap(role -> role.getPermissions().stream())
        .collect(Collectors.toSet());
  }

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
      if (member == null || CollectionUtils.isEmpty(member.getMemberTypes())) {
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
      if (member == null || CollectionUtils.isEmpty(member.getMemberTypes())) {
        return Set.of(RoleType.VIEWER);
      }
      return MemberTypeRoleMapper.getRolesForMemberTypes(member.getMemberTypes());
    }

    /**
     * Get all roles including both: 1. Roles derived from member types 2. Roles explicitly assigned
     * in UserAccount
     */
    @SuppressWarnings("PMD.UseEnumCollections")
    public Set<RoleType> getAllRoles() {
      final Set<RoleType> allRoles = new HashSet<>(getAllMemberRoles());
      if (userAccount.getRoles() != null) {
        allRoles.addAll(userAccount.getRoles());
      }
      return allRoles;
    }

    /**
     * Get all permissions including those from: 1. All member types (union of permissions) 2.
     * Explicitly assigned roles in UserAccount
     */
    @SuppressWarnings("PMD.UseEnumCollections")
    public Set<Permission> getAllPermissions() {
      final Set<Permission> permissions = new HashSet<>();

      // Add permissions from member types
      if (member != null && member.getMemberTypes() != null) {
        permissions.addAll(
            MemberTypeRoleMapper.getAllPermissionsForMemberTypes(member.getMemberTypes()));
      }

      permissions.addAll(userAccount.getPermissions());

      return permissions;
    }

    /** Check if user has any of the specified roles. */
    public boolean hasAnyRole(final RoleType... roles) {
      final Set<RoleType> userRoles = getAllRoles();
      return Arrays.stream(roles).anyMatch(userRoles::contains);
    }
  }
}
