package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.pages.programme.ProgrammeItem;
import com.wcc.platform.domain.platform.Event;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LandingPage {
  private Page heroSection;
  private Page fullBannerSection;
  private Section<ProgrammeItem> programmes;
  private Section<Event> announcements;
  private Section<Event> events;
  private Page volunteerSection;
}
