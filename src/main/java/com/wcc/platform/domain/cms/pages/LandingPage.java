package com.wcc.platform.domain.cms.pages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

  @JsonInclude(Include.NON_NULL)
  private Page fullBannerSection;

  private Section<ProgrammeItem> programmes;

  @JsonInclude(Include.NON_NULL)
  private Section<Event> announcements;

  @JsonInclude(Include.NON_NULL)
  private Section<Event> events;

  @JsonInclude(Include.NON_NULL)
  private Page volunteerSection;
}
