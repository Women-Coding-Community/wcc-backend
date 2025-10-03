package com.wcc.platform.domain.cms.attributes;

import lombok.AllArgsConstructor;
import lombok.Getter;

/* Allowed Programming languages */
@Getter
@AllArgsConstructor
public enum Languages {
  C_LANGUAGE("C", 1),
  C_PLUS_PLUS("C++", 2),
  C_SHARP("C#", 3),
  GO("Go", 4),
  JAVA("Java", 5),
  JAVASCRIPT("Javascript", 6),
  KOTLIN("Kotlin", 7),
  PHP("Php", 8),
  PYTHON("Python", 9),
  RUBY("Ruby", 10),
  RUST("Rust", 11),
  TYPESCRIPT("Typescript", 12),
  OTHER("Other", 13);

  private final String name;
  private final int langId;

  /** Find Language by name. */
  public static Languages fromName(final String name) {
    for (final Languages lang : values()) {
      if (lang.name.equalsIgnoreCase(name)) {
        return lang;
      }
    }
    throw new IllegalArgumentException("Unknown Language: " + name);
  }
}
