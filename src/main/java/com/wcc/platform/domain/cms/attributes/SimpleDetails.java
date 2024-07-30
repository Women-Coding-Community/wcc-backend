package com.wcc.platform.domain.cms.attributes;

/**
 * Details of a section or page.
 *
 * @param title
 * @param description
 * @param card
 */
public record SimpleDetails(String title, String description, Card card) {}
