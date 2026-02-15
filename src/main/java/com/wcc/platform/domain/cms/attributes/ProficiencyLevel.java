package com.wcc.platform.domain.cms.attributes;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing proficiency levels for programming languages.
 *
 * <p>Each enum instance represents a proficiency level with a specific name and an associated
 * unique identifier.
 */
@Schema(
    description =
        "Proficiency level can be populated using the enum NAME (e.g., BEGINNER), description (e.g., Beginner), or id (e.g., 1).",
    example = "ADVANCED")
@Getter
@AllArgsConstructor
public enum ProficiencyLevel {
  BEGINNER("Beginner", 1),
  INTERMEDIATE("Intermediate", 2),
  ADVANCED("Advanced", 3),
  EXPERT("Expert", 4);

  private final String description;
  private final Integer levelId;

  /** Find ProficiencyLevel by id. */
  public static ProficiencyLevel fromId(final Integer levelId) {
    for (final ProficiencyLevel level : values()) {
      if (level.levelId.equals(levelId)) {
        return level;
      }
    }
    throw new IllegalArgumentException("Invalid level id: " + levelId);
  }

  public static List<ProficiencyLevel> getAll() {
    return List.of(values());
  }

  @Override
  public String toString() {
    return description;
  }
}
