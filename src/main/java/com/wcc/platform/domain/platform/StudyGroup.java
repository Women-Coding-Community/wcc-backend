package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.LabelLink;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** StudyGroup class representing the structure of a study group. */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyGroup {
  @NotEmpty private String title;
  @NotEmpty private String description;
  @NotEmpty private String coordinators;
  @NotNull private LabelLink link;
  private Integer participants;
}
