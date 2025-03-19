package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ListSection;
import com.wcc.platform.domain.cms.pages.mentorship.FeedbackSection;
import com.wcc.platform.domain.cms.pages.programme.ProgrammeItem;
import com.wcc.platform.domain.platform.Event;
import com.wcc.platform.domain.platform.Partner;
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
  @NotNull private CommonSection fullBannerSection;
  @NotNull private ListSection<ProgrammeItem> programmes;
  private ListSection<Event> announcements;
  private ListSection<Event> events;
  private FeedbackSection feedbackSection;
  @NotNull private CommonSection volunteerSection;
  private ListSection<Partner> partners;
}
