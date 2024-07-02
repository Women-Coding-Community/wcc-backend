package com.wcc.platform.domain.cms.pages;

import java.util.List;

/** CMS Simple Section to be included in the pages. */
public record Section(String title, String description, List<String> items) {}
