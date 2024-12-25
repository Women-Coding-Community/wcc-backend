package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * CMS About Us page.
 *
 * @param heroSection Page details as title and images
 * @param items all details
 * @param contact all details
 */
public record AboutUsPage(
    @NotNull String id,
    @NotNull Page heroSection,
    @NotEmpty List<Section<String>> items,
    @NotEmpty Contact contact) {}
