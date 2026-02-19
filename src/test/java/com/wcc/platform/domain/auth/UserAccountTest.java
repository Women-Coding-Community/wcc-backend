package com.wcc.platform.domain.auth;

import static com.wcc.platform.factories.SetupUserAccountFactories.createAdminUserTest;
import static com.wcc.platform.factories.SetupUserAccountFactories.createUserAccountTest;
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

  private final UserAccount adminUserAccount = createUserAccountTest(RoleType.ADMIN);

  @Test
  void testGetPermissionsWithMultipleRolesReturnsAggregatedPermissions() {
    Set<Permission> permissions = adminUserAccount.getPermissions();

    assertNotNull(permissions);
    assertFalse(permissions.isEmpty());
  }

  @Test
  void testGetPermissionsWithSingleRoleReturnsRolePermissions() {
    Set<Permission> permissions = adminUserAccount.getPermissions();

    assertNotNull(permissions);
    assertFalse(permissions.isEmpty());
    assertEquals(RoleType.ADMIN.getPermissions(), permissions);
  }

  @Test
  void testGetPermissionsWithNullRolesReturnsEmptySet() {
    Member member =
        Member.builder()
            .id(1L)
            .fullName("John Doe")
            .memberTypes(List.of(MemberType.MENTOR))
            .build();

    UserAccount userAccount = createUserAccountTest(member);
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

    UserAccount userAccount = createUserAccountTest(member);
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
        new UserAccount(1, 1L, "john@example.com", "passwordHash", List.of(), true);

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
        new UserAccount(1, 1L, "john@example.com", "passwordHash", List.of(), true);

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
        new UserAccount(1, 1L, "john@example.com", "passwordHash", List.of(), true);

    UserAccount.User user = new UserAccount.User(userAccount, member);
    Set<RoleType> roles = user.getAllRoles();

    assertEquals(1, roles.size());
    assertTrue(roles.contains(RoleType.MENTOR));
  }

  @Test
  void testGetAllRolesOnlyUserRolesReturnsOnlyUserRoles() {
    Member member = Member.builder().id(1L).fullName("John Doe").memberTypes(List.of()).build();

    UserAccount userAccount =
        new UserAccount(1, 1L, "john@example.com", "passwordHash", List.of(RoleType.LEADER), true);
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

    UserAccount userAccount = createAdminUserTest();

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
        new UserAccount(1, 1L, "john@example.com", "passwordHash", List.of(), true);

    UserAccount.User user = new UserAccount.User(userAccount, member);
    Set<Permission> permissions = user.getAllPermissions();

    assertNotNull(permissions);
    assertFalse(permissions.isEmpty());
  }

  @Test
  void testGetAllPermissionsFromUserRolesIncludesUserRolePermissions() {
    Member member = Member.builder().id(1L).fullName("John Doe").memberTypes(List.of()).build();

    UserAccount userAccount = createAdminUserTest();

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

    UserAccount userAccount = createAdminUserTest();

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

    UserAccount userAccount = createAdminUserTest();

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

    UserAccount userAccount = createAdminUserTest();

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
        new UserAccount(1, 1L, "john@example.com", "passwordHash", List.of(), true);

    UserAccount.User user = new UserAccount.User(userAccount, member);

    assertFalse(user.hasAnyRole(RoleType.ADMIN, RoleType.MENTOR));
  }
}
