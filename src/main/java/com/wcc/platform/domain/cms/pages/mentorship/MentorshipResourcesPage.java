package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ListSection;
import com.wcc.platform.domain.cms.attributes.style.CustomStyle;
import jakarta.validation.constraints.NotNull;

/** Represents the Mentorship Resources page. */
public record MentorshipResourcesPage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    @NotNull CommonSection section,
    @NotNull ListSection<MentorshipResource> resourcesSection,
    @NotNull CustomStyle customStyle) {}
