package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ListSection;
import com.wcc.platform.domain.platform.LongTermTimelineEvent;
import jakarta.validation.constraints.NotNull;

/** Represents the MentorshipLong Term Timeline Page. */
public record LongTermTimeLinePage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    @NotNull ListSection<LongTermTimelineEvent> longTermTimelineEvents) {}
