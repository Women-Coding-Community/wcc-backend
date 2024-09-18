package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.platform.Filters;

/**
 * Filters section for any page.
 *
 * @param page {@link Page}
 * @param filters {@link Filters}
 */
public record FiltersSection(Page page, Filters filters) {}
