package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.FaqItem;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ListSection;
import jakarta.validation.constraints.NotNull;

/** Represents the Mentorship FAQ Page. */
public record MentorshipFaqPage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    @NotNull ListSection<FaqItem> commonFaqSection,
    @NotNull ListSection<FaqItem> mentorsFaqSection,
    @NotNull ListSection<FaqItem> menteesFaqSection) {}
