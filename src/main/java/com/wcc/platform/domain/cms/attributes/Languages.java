package com.wcc.platform.domain.cms.attributes;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Languages {
  JAVA("Java"),
  PYTHON("Python"),
  JAVASCRIPT("Javascript"),
  C_SHARP("C#"),
  RUBY("Ruby"),
  PHP("Php"),
  KOTLIN("Kotlin"),
  GO("Go"),
  RUST("Rust");

  private final String languageName;

  @Override
  @JsonValue
  public String toString() {
    return languageName;
  }
}
