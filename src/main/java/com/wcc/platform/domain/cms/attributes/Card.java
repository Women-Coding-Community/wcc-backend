package com.wcc.platform.domain.cms.attributes;

/**
 * Basic card details for a page or section.
 *
 * @param title of the card
 * @param subtitle on the card
 * @param description shortened description
 * @param link link for any external or internal resource
 */
public record Card(String title, String subtitle, String description, SimpleLink link) {}
