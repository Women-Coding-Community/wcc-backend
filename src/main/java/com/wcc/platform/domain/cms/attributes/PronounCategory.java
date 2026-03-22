package com.wcc.platform.domain.cms.attributes;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing categories of pronouns for filtering and grouping purposes.
 *
 * <p>The actual pronouns are stored as free text in the Member entity, and this enum is used for
 * categorization and filtering.
 */
@Getter
@AllArgsConstructor
public enum PronounCategory {
  FEMININE("Feminine", 1),
  MASCULINE("Masculine", 2),
  NEUTRAL("Neutral", 3),
  MULTIPLE("Multiple", 4),
  NEOPRONOUNS("Neopronouns", 5),
  ANY("Any", 6),
  UNSPECIFIED("Unspecified", 7);

  private final String name;
  private final int categoryId;

  /** Find PronounCategory by id. */
  public static PronounCategory fromId(final int categoryId) {
    for (final PronounCategory category : values()) {
      if (category.categoryId == categoryId) {
        return category;
      }
    }
    return UNSPECIFIED;
  }

  public static List<PronounCategory> getAll() {
    return List.of(values());
  }

  @Override
  public String toString() {
    return name;
  }
}
