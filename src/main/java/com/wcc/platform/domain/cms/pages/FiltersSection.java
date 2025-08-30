package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.pages.events.Filters;

/**
 * Filters section for any page.
 *
 * @param filters {@link Filters}
 */
public record FiltersSection(String title, Filters filters) {}
