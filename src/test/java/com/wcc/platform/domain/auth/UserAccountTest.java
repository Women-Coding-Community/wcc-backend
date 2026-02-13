package com.wcc.platform.domain.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.platform.type.RoleType;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserAccountTest {

  @Test
  void testGetPermissionsWithMultipleRolesReturnsAggregatedPermissions() {
    UserAccount userAccount =
        UserAccount.builder()
            .id(1)
            .email("user@example.com")
            .roles(List.of(RoleType.ADMIN, RoleType.MENTOR))
            .enabled(true)
            .build();

    Set<Permission> permissions = userAccount.getPermissions();

    assertNotNull(permissions);
    assertFalse(permissions.isEmpty());
  }

  @Test
  void testGetPermissionsWithSingleRoleReturnsRolePermissions() {
    UserAccount userAccount =
        UserAccount.builder()
            .id(1)
            .email("user@example.com")
            .roles(List.of(RoleType.ADMIN))
            .enabled(true)
            .build();

    Set<Permission> permissions = userAccount.getPermissions();

    assertNotNull(permissions);
    assertFalse(permissions.isEmpty());
    assertEquals(RoleType.ADMIN.getPermissions(), permissions);
  }

  @Test
  void testGetPermissionsWithNullRolesReturnsEmptySet() {
    UserAccount userAccount =
        UserAccount.builder().id(1).email("user@example.com").roles(null).enabled(true).build();

    Set<Permission> permissions = userAccount.getPermissions();

    assertNotNull(permissions);
    assertTrue(permissions.isEmpty());
  }

  @Test
  void testGetPrimaryRoleMemberWithSingleTypeReturnsCorrespondingRole() {
    Member member =
        Member.builder()
            .id(1L)
            .fullName("John Doe")
            .memberTypes(List.of(MemberType.MENTOR))
            .build();

    UserAccount userAccount =
        UserAccount.builder().id(1).memberId(1L).email("john@example.com").roles(List.of()).build();

    UserAccount.User user = new UserAccount.User(userAccount, member);

    assertEquals(RoleType.MENTOR, user.getPrimaryRole());
  }

  @Test
  void testGetPrimaryRoleMemberWithMultipleTypesReturnsHighestPrivilegeRole() {
    // DIRECTOR is higher privilege than MENTEE
    Member member =
        Member.builder()
            .id(1L)
            .fullName("John Doe")
            .memberTypes(List.of(MemberType.DIRECTOR, MemberType.MENTEE))
            .build();

    UserAccount userAccount =
        UserAccount.builder().id(1).memberId(1L).email("john@example.com").roles(List.of()).build();

    UserAccount.User user = new UserAccount.User(userAccount, member);

    assertEquals(RoleType.ADMIN, user.getPrimaryRole());
  }

  @Test
  void testGetAllMemberRolesMemberWithSingleTypeReturnsSingleRole() {
    Member member =
        Member.builder()
            .id(1L)
            .fullName("John Doe")
            .memberTypes(List.of(MemberType.MENTOR))
            .build();

    UserAccount userAccount =
        UserAccount.builder().id(1).memberId(1L).email("john@example.com").roles(List.of()).build();

    UserAccount.User user = new UserAccount.User(userAccount, member);
    Set<RoleType> roles = user.getAllMemberRoles();

    assertEquals(1, roles.size());
    assertTrue(roles.contains(RoleType.MENTOR));
  }

  @Test
  void testGetAllMemberRolesMemberWithMultipleTypesReturnsMultipleRoles() {
    Member member =
        Member.builder()
            .id(1L)
            .fullName("John Doe")
            .memberTypes(List.of(MemberType.MENTOR, MemberType.COLLABORATOR))
            .build();

    UserAccount userAccount =
        UserAccount.builder().id(1).memberId(1L).email("john@example.com").roles(List.of()).build();

    UserAccount.User user = new UserAccount.User(userAccount, member);
    Set<RoleType> roles = user.getAllMemberRoles();

    assertEquals(2, roles.size());
    assertTrue(roles.contains(RoleType.MENTOR));
    assertTrue(roles.contains(RoleType.CONTRIBUTOR));
  }

  @Test
  void testGetAllRolesOnlyMemberRolesReturnsOnlyMemberRoles() {
    Member member =
        Member.builder()
            .id(1L)
            .fullName("John Doe")
            .memberTypes(List.of(MemberType.MENTOR))
            .build();

    UserAccount userAccount =
        UserAccount.builder().id(1).memberId(1L).email("john@example.com").roles(List.of()).build();

    UserAccount.User user = new UserAccount.User(userAccount, member);
    Set<RoleType> roles = user.getAllRoles();

    assertEquals(1, roles.size());
    assertTrue(roles.contains(RoleType.MENTOR));
  }

  @Test
  void testGetAllRolesOnlyUserRolesReturnsOnlyUserRoles() {
    Member member = Member.builder().id(1L).fullName("John Doe").memberTypes(List.of()).build();

    UserAccount userAccount =
        UserAccount.builder()
            .id(1)
            .memberId(1L)
            .email("john@example.com")
            .roles(List.of(RoleType.LEADER))
            .build();

    UserAccount.User user = new UserAccount.User(userAccount, member);
    Set<RoleType> roles = user.getAllRoles();

    assertEquals(2, roles.size());
    assertTrue(roles.contains(RoleType.LEADER));
  }

  @Test
  void testGetAllRolesCombinedMemberAndUserRolesReturnsAggregatedRoles() {
    Member member =
        Member.builder()
            .id(1L)
            .fullName("John Doe")
            .memberTypes(List.of(MemberType.MENTOR))
            .build();

    UserAccount userAccount =
        UserAccount.builder()
            .id(1)
            .memberId(1L)
            .email("john@example.com")
            .roles(List.of(RoleType.ADMIN))
            .build();

    UserAccount.User user = new UserAccount.User(userAccount, member);
    Set<RoleType> roles = user.getAllRoles();

    assertEquals(2, roles.size());
    assertTrue(roles.contains(RoleType.MENTOR));
    assertTrue(roles.contains(RoleType.ADMIN));
  }

  @Test
  void testGetAllPermissionsFromMemberTypesIncludesMemberTypePermissions() {
    Member member =
        Member.builder()
            .id(1L)
            .fullName("John Doe")
            .memberTypes(List.of(MemberType.MENTOR))
            .build();

    UserAccount userAccount =
        UserAccount.builder().id(1).memberId(1L).email("john@example.com").roles(List.of()).build();

    UserAccount.User user = new UserAccount.User(userAccount, member);
    Set<Permission> permissions = user.getAllPermissions();

    assertNotNull(permissions);
    assertFalse(permissions.isEmpty());
  }

  @Test
  void testGetAllPermissionsFromUserRolesIncludesUserRolePermissions() {
    Member member = Member.builder().id(1L).fullName("John Doe").memberTypes(List.of()).build();

    UserAccount userAccount =
        UserAccount.builder()
            .id(1)
            .memberId(1L)
            .email("john@example.com")
            .roles(List.of(RoleType.ADMIN))
            .build();

    UserAccount.User user = new UserAccount.User(userAccount, member);
    Set<Permission> permissions = user.getAllPermissions();

    assertNotNull(permissions);
    assertFalse(permissions.isEmpty());
  }

  @Test
  void testGetAllPermissionsCombinedPermissionsAggregatesFromBothSources() {
    Member member =
        Member.builder()
            .id(1L)
            .fullName("John Doe")
            .memberTypes(List.of(MemberType.MENTOR))
            .build();

    UserAccount userAccount =
        UserAccount.builder()
            .id(1)
            .memberId(1L)
            .email("john@example.com")
            .roles(List.of(RoleType.ADMIN))
            .build();

    UserAccount.User user = new UserAccount.User(userAccount, member);
    Set<Permission> permissions = user.getAllPermissions();

    assertNotNull(permissions);
    assertFalse(permissions.isEmpty());
    // Should include permissions from both MENTOR role and ADMIN role
    assertTrue(permissions.size() >= 2);
  }

  @Test
  void testHasAnyRoleUserHasOneOfRequiredRolesReturnsTrue() {
    Member member =
        Member.builder()
            .id(1L)
            .fullName("John Doe")
            .memberTypes(List.of(MemberType.MENTOR))
            .build();

    UserAccount userAccount =
        UserAccount.builder()
            .id(1)
            .memberId(1L)
            .email("john@example.com")
            .roles(List.of(RoleType.ADMIN))
            .build();

    UserAccount.User user = new UserAccount.User(userAccount, member);

    assertTrue(user.hasAnyRole(RoleType.ADMIN, RoleType.VIEWER));
  }

  @Test
  void testHasAnyRoleUserHasMultipleRequiredRolesReturnsTrue() {
    Member member =
        Member.builder()
            .id(1L)
            .fullName("John Doe")
            .memberTypes(List.of(MemberType.MENTOR))
            .build();

    UserAccount userAccount =
        UserAccount.builder()
            .id(1)
            .memberId(1L)
            .email("john@example.com")
            .roles(List.of(RoleType.ADMIN))
            .build();

    UserAccount.User user = new UserAccount.User(userAccount, member);

    assertTrue(user.hasAnyRole(RoleType.ADMIN, RoleType.MENTOR));
  }

  @Test
  void testHasAnyRoleUserHasNoneOfRequiredRolesReturnsFalse() {
    Member member =
        Member.builder()
            .id(1L)
            .fullName("John Doe")
            .memberTypes(List.of(MemberType.MENTEE))
            .build();

    UserAccount userAccount =
        UserAccount.builder().id(1).memberId(1L).email("john@example.com").roles(List.of()).build();

    UserAccount.User user = new UserAccount.User(userAccount, member);

    assertFalse(user.hasAnyRole(RoleType.ADMIN, RoleType.MENTOR));
  }
}
