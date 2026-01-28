package com.wcc.platform.configuration.security;

import com.wcc.platform.domain.auth.Permission;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequiresPermission {
  Permission[] value();

  LogicalOperator operator() default LogicalOperator.AND;
}
