package com.wcc.platform.domain.cms;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Define existent pages types. */
@Getter
@AllArgsConstructor
@SuppressWarnings("PMD.LongVariable")
public enum PageType {
  EVENTS("init-data/eventsPage.json", "page:EVENTS"),
  FOOTER("init-data/footerPage.json", "page:FOOTER"),
  LANDING_PAGE("init-data/landingPage.json", "page:LANDING_PAGE"),
  MENTORSHIP("init-data/mentorshipPage.json", "page:MENTORSHIP_OVERVIEW"),
  MENTORS("init-data/mentorsPage.json", "page:MENTORS_PAGE"),
  MENTORSHIP_FAQ("init-data/mentorshipFaqPage.json", "page:MENTORSHIP_FAQ"),
  MENTORSHIP_CONDUCT("init-data/mentorshipConductPage.json", "page:MENTORSHIP_CODE_OF_CONDUCT"),
  PROG_BOOK_CLUB("init-data/bookClubPage.json", "page:BOOK_CLUB"),
  EVENT_FILTERS("init-data/eventsFiltersSection.json", "UNDEFINED"),
  ABOUT_US("init-data/aboutUsPage.json", "page:ABOUT_US"),
  TEAM("init-data/teamPage.json", "page:TEAM"),
  COLLABORATOR("init-data/collaboratorPage.json", "page:COLLABORATORS"),
  CODE_OF_CONDUCT("init-data/codeOfConductPage.json", "page:CODE_OF_CONDUCT"),
  CELEBRATE_HER("init-data/celebrateHerPage.json", "page:CELEBRATE_HER"),
  PARTNERS("init-data/partnersPage.json", "page:PARTNERS"),
  STUDY_GROUPS("init-data/mentorshipStudyGroupsPage.json", "page:STUDY_GROUPS"),
  AD_HOC_TIMELINE("init-data/adHocTimelinePage.json", "page:AD_HOC_TIMELINE");

  public static final String ID_PREFIX = "page:";

  private final String fileName;
  private final String id;
}
