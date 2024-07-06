package com.wcc.platform.domain.cms.pages;

import java.util.List;

/**
 * CMS Code of conduct page.
 *
 * @param page Page details as title and description
 * @param items all details
 */
public record CodeOfConductPage(Page page, List<Section> items) {}
