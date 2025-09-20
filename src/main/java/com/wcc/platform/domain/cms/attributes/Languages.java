package com.wcc.platform.domain.cms.attributes;

import lombok.AllArgsConstructor;
import lombok.Getter;

/* Allowed Programming languages */
@Getter
@AllArgsConstructor
public enum Languages {
  C_LANGUAGE(1),
  C_PLUS_PLUS(2),
  C_SHARP(3),
  GO(4),
  JAVA(5),
  JAVASCRIPT(6),
  KOTLIN(7),
  PHP(8),
  PYTHON(9),
  RUBY(10),
  RUST(11);

  private final int id;
}
