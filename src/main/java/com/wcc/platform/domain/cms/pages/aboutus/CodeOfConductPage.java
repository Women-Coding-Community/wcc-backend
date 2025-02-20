package com.wcc.platform.domain.cms.pages.aboutus;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ListSection;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * CMS Code of conduct page.
 *
 * @param section CommonSection details as title and description
 * @param items all details
 */
public record CodeOfConductPage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    @NotNull CommonSection section,
    @NotEmpty List<ListSection<String>> items) {}
