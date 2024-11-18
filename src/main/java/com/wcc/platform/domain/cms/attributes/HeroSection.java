package com.wcc.platform.domain.cms.attributes;

import jakarta.validation.constraints.NotNull;

/**
 * Intro section.
 *
 * @param title title of section
 * @param description section description not mandatory
 * @param image mandatory image
 */
public record HeroSection(@NotNull String title, String description, @NotNull Image image) {}
