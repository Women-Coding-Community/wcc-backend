package com.wcc.platform.domain.platform;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** LongTermTimelineEvent class representing the structure of event timeline. */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LongTermTimelineEvent {
  @NotEmpty private String duration;
  @NotEmpty private String title;
  @NotEmpty private String description;
}
