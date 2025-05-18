package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Mentor;
import com.wcc.platform.domain.cms.attributes.Participants;
import jakarta.validation.constraints.NotEmpty;
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
  @NotEmpty private Mentor mentor;
  @NotEmpty private Participants participants;
}
