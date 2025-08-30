package com.wcc.platform.domain.cms.pages.aboutus;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** CMS Celebrate Her page. */
public record CelebrateHerPage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    @NotNull CommonSection section,
    @NotNull List<AboutHer> items) {}
