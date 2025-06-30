package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ListSection;
import com.wcc.platform.domain.platform.AdHocTimelineEvents;
import jakarta.validation.constraints.NotNull;

/**
 * CMS Ad Hoc Timeline Page details.
 *
 * @param heroSection hero section to be shown on the ad hoc timeline page
 * @param events section to explain the timeline for applying for an ad hoc mentor
 */
public record MentorshipAdHocTimelinePage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    @NotNull ListSection<AdHocTimelineEvents> events) {}
