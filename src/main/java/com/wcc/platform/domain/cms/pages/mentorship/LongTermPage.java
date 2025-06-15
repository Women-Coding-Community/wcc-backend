package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ListSection;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** Represents the Mentorship Timeline Page. */
public record LongTermPage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    @NotNull CommonSection section,
    @NotNull List<ListSection<String>> items) {}
