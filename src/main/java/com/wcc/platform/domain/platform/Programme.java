package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Programme class representing the structure of any programme section of a programme page. */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class Programme {
  @NotBlank private String title;
  @NotBlank private String description;
  @NotNull private CommonSection card;
}
