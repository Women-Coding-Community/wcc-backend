package com.wcc.platform.domain.cms.pages;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import java.util.List;

/** CMS Simple Section to be included in the pages. */
public record Section<T>(
    String title,
    String description,
    @JsonInclude(Include.NON_NULL) LabelLink link,
    List<T> items) {}
