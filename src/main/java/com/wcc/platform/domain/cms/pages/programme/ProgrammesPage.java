package com.wcc.platform.domain.cms.pages.programme;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ProgrammesSection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class ProgrammesPage {
  @NotBlank private String id;
  @NotNull private HeroSection heroSection;
  @NotNull private CommonSection section;
  @NotNull private ProgrammesSection programmesSection;
}
