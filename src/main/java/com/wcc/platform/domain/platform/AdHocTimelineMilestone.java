package com.wcc.platform.domain.platform;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** AdHocTimelineMilestone class representing the structure of an ad hoc timeline. */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdHocTimelineMilestone {
  @NotEmpty private String title;
  @NotEmpty private String description;
}
