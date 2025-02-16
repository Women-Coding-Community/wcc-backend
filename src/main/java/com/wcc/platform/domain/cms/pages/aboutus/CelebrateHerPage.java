package com.wcc.platform.domain.cms.pages.aboutus;

import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.platform.AboutHer;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** CMS Celebrate Her page. */
public record CelebrateHerPage(
    @NotNull String id, @NotNull HeroSection heroSection, @NotNull List<AboutHer> data) {}
