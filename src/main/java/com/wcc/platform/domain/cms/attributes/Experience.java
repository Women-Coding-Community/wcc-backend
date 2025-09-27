package com.wcc.platform.domain.cms.attributes;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Experience {
  NO_EXPERIENCE("No experience"),
  YEARS_1_TO_5("1-5 years"),
  YEARS_5_TO_10("5-10 years"),
  YEARS_10_TO_15("10-15 years"),
  OVER_15("15+ years");

  @NotBlank private final String experienceRange;

  public static Experience fromYears(Integer years) {
    if (years == null || years <= 0) return NO_EXPERIENCE;
    return switch (years) {
      case 1, 2, 3, 4, 5 -> YEARS_1_TO_5;
      case 6, 7, 8, 9, 10 -> YEARS_5_TO_10;
      case 11, 12, 13, 14, 15 -> YEARS_10_TO_15;
      default -> OVER_15;
    };
  }

  @Override
  @JsonValue
  public String toString() {
    return experienceRange;
  }
}
