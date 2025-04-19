package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Partner class with common attributes for all partners. */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Partner {
  @NotEmpty private Image image;
  @NotBlank private String name;
  private String description;
  @NotNull private LabelLink link;
}
