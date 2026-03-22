package com.wcc.platform.domain.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.platform.type.RoleType;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MemberTypeRoleMapperTest {

  @Test
  void testGetRoleForMemberTypeReturnsMappedRole() {
    assertEquals(RoleType.ADMIN, MemberTypeRoleMapper.getRoleForMemberType(MemberType.DIRECTOR));
    assertEquals(RoleType.LEADER, MemberTypeRoleMapper.getRoleForMemberType(MemberType.LEADER));
    assertEquals(RoleType.MENTOR, MemberTypeRoleMapper.getRoleForMemberType(MemberType.MENTOR));
    assertEquals(RoleType.MENTEE, MemberTypeRoleMapper.getRoleForMemberType(MemberType.MENTEE));
    assertEquals(
        RoleType.CONTRIBUTOR, MemberTypeRoleMapper.getRoleForMemberType(MemberType.COLLABORATOR));
    assertEquals(RoleType.VIEWER, MemberTypeRoleMapper.getRoleForMemberType(MemberType.MEMBER));
  }

  @Test
  void testGetRoleForMemberTypeNullThrowsIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class, () -> MemberTypeRoleMapper.getRoleForMemberType(null));
  }

  @Test
  void testGetRolesForMemberTypesNullOrEmptyReturnsViewer() {
    Set<RoleType> fromNull = MemberTypeRoleMapper.getRolesForMemberTypes(null);
    assertEquals(1, fromNull.size());
    assertTrue(fromNull.contains(RoleType.VIEWER));

    Set<RoleType> fromEmpty = MemberTypeRoleMapper.getRolesForMemberTypes(List.of());
    assertEquals(1, fromEmpty.size());
    assertTrue(fromEmpty.contains(RoleType.VIEWER));
  }

  @Test
  void testGetRolesForMemberTypesCollectsRoles() {
    Set<RoleType> roles =
        MemberTypeRoleMapper.getRolesForMemberTypes(
            List.of(MemberType.LEADER, MemberType.MENTEE, MemberType.SPEAKER));
    assertTrue(roles.contains(RoleType.LEADER));
    assertTrue(roles.contains(RoleType.MENTEE));
    assertTrue(roles.contains(RoleType.CONTRIBUTOR));
  }

  @Test
  void testGetHighestRoleReturnsMostPrivileged() {
    // Director maps to SUPER_ADMIN (highest), Member maps to VIEWER (low)
    RoleType highest =
        MemberTypeRoleMapper.getHighestRole(List.of(MemberType.MEMBER, MemberType.DIRECTOR));
    assertEquals(RoleType.ADMIN, highest);

    // Leader(Admin) vs Mentor -> ADMIN is higher in defined hierarchy
    highest = MemberTypeRoleMapper.getHighestRole(List.of(MemberType.MENTOR, MemberType.LEADER));
    assertEquals(RoleType.LEADER, highest);
  }

  @Test
  void testGetHighestRoleNullOrEmptyReturnsViewer() {
    assertEquals(RoleType.VIEWER, MemberTypeRoleMapper.getHighestRole(null));
    assertEquals(RoleType.VIEWER, MemberTypeRoleMapper.getHighestRole(List.of()));
  }

  @Test
  void testGetAllPermissionsForMemberTypesNullOrEmptyReturnsViewerPermissions() {
    Set<Permission> viewerPerms = RoleType.VIEWER.getPermissions();

    Set<Permission> fromNull = MemberTypeRoleMapper.getAllPermissionsForMemberTypes(null);
    assertEquals(viewerPerms, fromNull);

    Set<Permission> fromEmpty = MemberTypeRoleMapper.getAllPermissionsForMemberTypes(List.of());
    assertEquals(viewerPerms, fromEmpty);
  }

  @Test
  void testIsSuperAdminChecksCorrectly() {
    assertTrue(MemberTypeRoleMapper.isSuperAdmin(List.of(MemberType.DIRECTOR)));
    assertFalse(MemberTypeRoleMapper.isSuperAdmin(List.of(MemberType.MEMBER, MemberType.MENTEE)));
    assertFalse(MemberTypeRoleMapper.isSuperAdmin(null));
  }

  @Test
  void testIsAdminChecksCorrectly() {
    assertTrue(MemberTypeRoleMapper.isAdmin(List.of(MemberType.LEADER)));
    // Director (SUPER_ADMIN) should also count as admin
    assertTrue(MemberTypeRoleMapper.isAdmin(List.of(MemberType.DIRECTOR)));
    assertFalse(MemberTypeRoleMapper.isAdmin(List.of(MemberType.MEMBER)));
    assertFalse(MemberTypeRoleMapper.isAdmin(null));
  }

  @Test
  void testHasRoleChecksCorrectly() {
    assertTrue(
        MemberTypeRoleMapper.hasRole(
            List.of(MemberType.LEADER, MemberType.MEMBER), RoleType.LEADER));
    assertFalse(MemberTypeRoleMapper.hasRole(List.of(MemberType.MEMBER), RoleType.LEADER));
    assertFalse(MemberTypeRoleMapper.hasRole(null, RoleType.LEADER));
    assertFalse(MemberTypeRoleMapper.hasRole(List.of(MemberType.LEADER), null));
  }
}
