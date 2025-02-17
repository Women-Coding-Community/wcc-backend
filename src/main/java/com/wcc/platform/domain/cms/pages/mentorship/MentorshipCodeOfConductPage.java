package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ListSection;
import jakarta.validation.constraints.NotNull;

/** Represents the Mentorship Code of Conduct Page. */
public record MentorshipCodeOfConductPage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    ListSection<String> menteeCodeSection,
    ListSection<String> mentorCodeSection,
    CommonSection wccCodeSection) {}
