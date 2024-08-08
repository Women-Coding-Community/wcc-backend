package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.pages.programme.ProgrammeItem;
import com.wcc.platform.domain.platform.Event;
import lombok.Builder;
import lombok.Data;

/** Landing Page sections. */
@Data
@Builder
public class LandingPage {
  Page heroSection;
  Page fullBannerSection;
  Section<ProgrammeItem> programmes;
  Section<Event> announcements;
  Section<Event> events;
  Page volunteerSection;

  public LandingPage(
      Page heroSection,
      Page fullBannerSection,
      Section<ProgrammeItem> programmes,
      Section<Event> announcements,
      Section<Event> events,
      Page volunteerSection) {
    this.heroSection = heroSection;
    this.fullBannerSection = fullBannerSection;
    this.programmes = programmes;
    this.announcements = announcements;
    this.events = events;
    this.volunteerSection = volunteerSection;
  }

  public LandingPage() {
    // Necessary constructor for jackson.
  }
}
