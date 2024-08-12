package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.LabelLink;
import java.util.List;

/** CMS Simple Section to be included in the pages. */
public record Section<T>(
    String title,
    String description,
    LabelLink link,
    List<T> items) {}
