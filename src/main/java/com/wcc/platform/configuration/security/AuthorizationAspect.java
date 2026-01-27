package com.wcc.platform.configuration.security;

import com.wcc.platform.domain.auth.Permission;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.service.AuthService;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
@AllArgsConstructor
public class AuthorizationAspect {

  private final AuthService authService;

  @Around("@annotation(requiresPermission)")
  public Object checkPermission(
      ProceedingJoinPoint joinPoint, RequiresPermission requiresPermission) throws Throwable {

    Permission[] permissions = requiresPermission.value();

    if (permissions.length == 0) {
      throw new IllegalArgumentException(
          "@RequiresPermission must specify at least one permission");
    }

    if (requiresPermission.operator() == LogicalOperator.AND) {
      authService.requireAllPermissions(permissions);
    } else {
      authService.requireAnyPermission(permissions);
    }

    return joinPoint.proceed();
  }

  @Around("@annotation(requiresRole)")
  public Object checkRole(ProceedingJoinPoint joinPoint, RequiresRole requiresRole)
      throws Throwable {

    RoleType[] roles = requiresRole.value();

    if (roles.length == 0) {
      throw new IllegalArgumentException("@RequiresRole must specify at least one role");
    }

    authService.requireRole(roles);

    return joinPoint.proceed();
  }
}
