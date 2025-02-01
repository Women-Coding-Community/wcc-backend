package com.wcc.platform.domain.cms.pages.programme;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.style.CustomStyle;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.platform.EventSection;
import com.wcc.platform.domain.platform.Programme;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** BookClub programme details. */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class ProgrammePage {
  @NotBlank private String id;
  @NotNull private HeroSection heroSection;
  @NotNull private Page page;
  @NotNull private Contact contact;
  @NotEmpty private List<Programme> programmeDetails;
  @NotNull private EventSection eventSection;
  @NotNull private CustomStyle customStyle;
}
