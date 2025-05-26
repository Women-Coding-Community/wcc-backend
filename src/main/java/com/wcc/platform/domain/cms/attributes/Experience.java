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

  @Override
  @JsonValue
  public String toString() {
    return experienceRange;
  }
}
