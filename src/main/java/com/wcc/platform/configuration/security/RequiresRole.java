package com.wcc.platform.configuration.security;

import com.wcc.platform.domain.platform.type.RoleType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation to enforce role-based access control on methods or classes. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequiresRole {

  /** The roles required to access the annotated method or class. */
  RoleType[] value();

  /**
   * Defines the logical operator to apply when multiple roles are specified. By default, it uses OR
   */
  LogicalOperator operator() default LogicalOperator.OR;
}
