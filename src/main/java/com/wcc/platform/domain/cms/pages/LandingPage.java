package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.pages.programme.ProgrammeItem;
import com.wcc.platform.domain.platform.Event;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Landing Page sections. */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandingPage {
  @NotNull private String id;
  @NotNull private HeroSection heroSection;
  @NotNull private Page fullBannerSection;
  @NotNull private Section<ProgrammeItem> programmes;
  private Section<Event> announcements;
  private Section<Event> events;
  @NotNull private Page volunteerSection;
}
