package com.wcc.platform.configuration.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.auth.Permission;
import com.wcc.platform.domain.exceptions.ForbiddenException;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.service.AuthService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorizationAspectTest {

  @Mock private AuthService authService;
  @Mock private ProceedingJoinPoint joinPoint;
  @Mock private RequiresPermission requiresPermission;
  @Mock private RequiresRole requiresRole;

  private AuthorizationAspect authorizationAspect;

  @BeforeEach
  void setUp() {
    authorizationAspect = new AuthorizationAspect(authService);
  }

  @Test
  void testCheckPermissionAndOperatorAllPermissionsGrantedProceedsWithMethod() throws Throwable {
    Permission[] permissions = {Permission.MENTOR_APPROVE, Permission.MENTEE_APPROVE};
    when(requiresPermission.value()).thenReturn(permissions);
    when(requiresPermission.operator()).thenReturn(LogicalOperator.AND);
    when(joinPoint.proceed()).thenReturn("success");

    Object result = authorizationAspect.checkPermission(joinPoint, requiresPermission);

    assertEquals("success", result);
    verify(authService).requireAllPermissions(permissions);
    verify(joinPoint).proceed();
  }

  @Test
  void testCheckPermissionAndOperatorPermissionDeniedThrowsForbiddenException() throws Throwable {
    Permission[] permissions = {Permission.MENTOR_APPROVE, Permission.MENTEE_APPROVE};
    when(requiresPermission.value()).thenReturn(permissions);
    when(requiresPermission.operator()).thenReturn(LogicalOperator.AND);
    doThrow(new ForbiddenException("Permission denied"))
        .when(authService)
        .requireAllPermissions(permissions);

    assertThrows(
        ForbiddenException.class,
        () -> authorizationAspect.checkPermission(joinPoint, requiresPermission));

    verify(authService).requireAllPermissions(permissions);
    verify(joinPoint, never()).proceed();
  }

  @Test
  void testCheckPermissionOrOperatorAnyPermissionGrantedProceedsWithMethod() throws Throwable {
    Permission[] permissions = {Permission.MENTOR_APPROVE, Permission.MENTEE_APPROVE};
    when(requiresPermission.value()).thenReturn(permissions);
    when(requiresPermission.operator()).thenReturn(LogicalOperator.OR);
    when(joinPoint.proceed()).thenReturn("success");

    Object result = authorizationAspect.checkPermission(joinPoint, requiresPermission);

    assertEquals("success", result);
    verify(authService).requireAnyPermission(permissions);
    verify(joinPoint).proceed();
  }

  @Test
  void testCheckRoleSingleRoleGrantedProceedsWithMethod() throws Throwable {
    RoleType[] roles = {RoleType.ADMIN};
    when(requiresRole.value()).thenReturn(roles);
    when(joinPoint.proceed()).thenReturn("success");

    Object result = authorizationAspect.checkRole(joinPoint, requiresRole);

    assertEquals("success", result);
    verify(authService).requireRole(roles);
    verify(joinPoint).proceed();
  }

  @Test
  void testCheckRoleMultipleRolesGrantedProceedsWithMethod() throws Throwable {
    RoleType[] roles = {RoleType.ADMIN, RoleType.LEADER, RoleType.MENTOR};
    when(requiresRole.value()).thenReturn(roles);
    when(joinPoint.proceed()).thenReturn("success");

    Object result = authorizationAspect.checkRole(joinPoint, requiresRole);

    assertEquals("success", result);
    verify(authService).requireRole(roles);
    verify(joinPoint).proceed();
  }

  @Test
  void testCheckRoleRoleDeniedThrowsForbiddenException() throws Throwable {
    RoleType[] roles = {RoleType.ADMIN};
    when(requiresRole.value()).thenReturn(roles);
    doThrow(new ForbiddenException("Role not granted")).when(authService).requireRole(roles);

    assertThrows(
        ForbiddenException.class, () -> authorizationAspect.checkRole(joinPoint, requiresRole));

    verify(authService).requireRole(roles);
    verify(joinPoint, never()).proceed();
  }
}
