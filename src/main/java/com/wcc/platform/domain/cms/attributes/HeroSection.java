package com.wcc.platform.domain.cms.attributes;

/**
 * Intro section.
 *
 * @param title title of section
 * @param description section description not mandatory
 * @param image mandatory image
 */
public record HeroSection(String title, String description, Image image) {}
