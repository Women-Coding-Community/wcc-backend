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
  void getRoleForMemberType_returnsMappedRole() {
    assertEquals(
        RoleType.SUPER_ADMIN, MemberTypeRoleMapper.getRoleForMemberType(MemberType.DIRECTOR));
    assertEquals(RoleType.ADMIN, MemberTypeRoleMapper.getRoleForMemberType(MemberType.LEADER));
    assertEquals(RoleType.MENTOR, MemberTypeRoleMapper.getRoleForMemberType(MemberType.MENTOR));
    assertEquals(RoleType.MENTEE, MemberTypeRoleMapper.getRoleForMemberType(MemberType.MENTEE));
    assertEquals(
        RoleType.CONTRIBUTOR, MemberTypeRoleMapper.getRoleForMemberType(MemberType.COLLABORATOR));
    assertEquals(RoleType.VIEWER, MemberTypeRoleMapper.getRoleForMemberType(MemberType.MEMBER));
  }

  @Test
  void getRoleForMemberType_null_throwsIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class, () -> MemberTypeRoleMapper.getRoleForMemberType(null));
  }

  @Test
  void getRolesForMemberTypes_nullOrEmpty_returnsViewer() {
    Set<RoleType> fromNull = MemberTypeRoleMapper.getRolesForMemberTypes(null);
    assertEquals(1, fromNull.size());
    assertTrue(fromNull.contains(RoleType.VIEWER));

    Set<RoleType> fromEmpty = MemberTypeRoleMapper.getRolesForMemberTypes(List.of());
    assertEquals(1, fromEmpty.size());
    assertTrue(fromEmpty.contains(RoleType.VIEWER));
  }

  @Test
  void getRolesForMemberTypes_collectsRoles() {
    Set<RoleType> roles =
        MemberTypeRoleMapper.getRolesForMemberTypes(
            List.of(MemberType.LEADER, MemberType.MENTEE, MemberType.SPEAKER));
    assertTrue(roles.contains(RoleType.ADMIN));
    assertTrue(roles.contains(RoleType.MENTEE));
    assertTrue(roles.contains(RoleType.CONTRIBUTOR));
  }

  @Test
  void getHighestRole_returnsMostPrivileged() {
    // Director maps to SUPER_ADMIN (highest), Member maps to VIEWER (low)
    RoleType highest =
        MemberTypeRoleMapper.getHighestRole(List.of(MemberType.MEMBER, MemberType.DIRECTOR));
    assertEquals(RoleType.SUPER_ADMIN, highest);

    // Leader(Admin) vs Mentor -> ADMIN is higher in defined hierarchy
    highest = MemberTypeRoleMapper.getHighestRole(List.of(MemberType.MENTOR, MemberType.LEADER));
    assertEquals(RoleType.ADMIN, highest);
  }

  @Test
  void getHighestRole_nullOrEmpty_returnsViewer() {
    assertEquals(RoleType.VIEWER, MemberTypeRoleMapper.getHighestRole(null));
    assertEquals(RoleType.VIEWER, MemberTypeRoleMapper.getHighestRole(List.of()));
  }

  @Test
  void getAllPermissionsForMemberTypes_nullOrEmpty_returnsViewerPermissions() {
    Set<Permission> viewerPerms = RoleType.VIEWER.getPermissions();

    Set<Permission> fromNull = MemberTypeRoleMapper.getAllPermissionsForMemberTypes(null);
    assertEquals(viewerPerms, fromNull);

    Set<Permission> fromEmpty = MemberTypeRoleMapper.getAllPermissionsForMemberTypes(List.of());
    assertEquals(viewerPerms, fromEmpty);
  }

  @Test
  void isSuperAdmin_checksCorrectly() {
    assertTrue(MemberTypeRoleMapper.isSuperAdmin(List.of(MemberType.DIRECTOR)));
    assertFalse(MemberTypeRoleMapper.isSuperAdmin(List.of(MemberType.MEMBER, MemberType.MENTEE)));
    assertFalse(MemberTypeRoleMapper.isSuperAdmin(null));
  }

  @Test
  void isAdmin_checksCorrectly() {
    assertTrue(MemberTypeRoleMapper.isAdmin(List.of(MemberType.LEADER)));
    // Director (SUPER_ADMIN) should also count as admin
    assertTrue(MemberTypeRoleMapper.isAdmin(List.of(MemberType.DIRECTOR)));
    assertFalse(MemberTypeRoleMapper.isAdmin(List.of(MemberType.MEMBER)));
    assertFalse(MemberTypeRoleMapper.isAdmin(null));
  }

  @Test
  void hasRole_checksCorrectly() {
    assertTrue(
        MemberTypeRoleMapper.hasRole(
            List.of(MemberType.LEADER, MemberType.MEMBER), RoleType.ADMIN));
    assertFalse(MemberTypeRoleMapper.hasRole(List.of(MemberType.MEMBER), RoleType.ADMIN));
    assertFalse(MemberTypeRoleMapper.hasRole(null, RoleType.ADMIN));
    assertFalse(MemberTypeRoleMapper.hasRole(List.of(MemberType.LEADER), null));
  }
}
