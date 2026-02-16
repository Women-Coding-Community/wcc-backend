package com.wcc.platform.domain.cms.attributes;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing various programming languages.
 *
 * <p>Each enum instance represents a programming language with a specific name and an associated
 * unique identifier.
 */
@Getter
@AllArgsConstructor
public enum CodeLanguage {
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
  public static CodeLanguage fromId(final int languageId) {
    for (final CodeLanguage lang : values()) {
      if (lang.langId == languageId) {
        return lang;
      }
    }
    return OTHER;
  }

  public static List<CodeLanguage> getAll() {
    return List.of(values());
  }

  @Override
  public String toString() {
    return name;
  }
}
