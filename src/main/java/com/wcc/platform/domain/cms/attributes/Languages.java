package com.wcc.platform.domain.cms.attributes;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/* Allowed Programming languages */
@Getter
@AllArgsConstructor
public enum Languages {
  C_LANGUAGE("C"),
  C_PLUS_PLUS("C++"),
  C_SHARP("C#"),
  GO("Go"),
  JAVA("Java"),
  JAVASCRIPT("Javascript"),
  KOTLIN("Kotlin"),
  PHP("Php"),
  PYTHON("Python"),
  RUBY("Ruby"),
  RUST("Rust");

  private final String languageName;

  @Override
  @JsonValue
  public String toString() {
    return languageName;
  }
}
