package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.MemberByType;
import jakarta.validation.constraints.NotNull;

/** CMS Community Core Team Page grouped by members types. */
public record TeamPage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    @NotNull CommonSection commonSection,
    @NotNull Contact contact,
    @NotNull MemberByType membersByType) {}
