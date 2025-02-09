package com.wcc.platform.domain.cms.pages.aboutUs;

import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.platform.AboutHer;
import jakarta.validation.constraints.NotNull;
import java.util.List;


/**
 * CMS Celebrate Her page.
 *
 * @param page Page details as title and images
 * @param members all details
 */
public record CelebrateHerPage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    @NotNull Page page,
    @NotNull List<AboutHer> data
) {}
