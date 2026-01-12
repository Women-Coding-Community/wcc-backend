package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** MentorshipResource class representing the structure of a mentorship resource. */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MentorshipResource {
  @NotEmpty private String title;
  @NotNull private LabelLink link;
  @NotNull private Image image;
}
