package com.wcc.platform.domain.cms.attributes;

import com.wcc.platform.domain.cms.attributes.style.CustomStyle;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Intro section.
 *
 * @param title title of section
 * @param subtitle section description not mandatory
 * @param images not mandatory images
 * @param customStyle custom style
 */
public record HeroSection(
    @NotNull String title, String subtitle, List<Image> images, CustomStyle customStyle) {}
