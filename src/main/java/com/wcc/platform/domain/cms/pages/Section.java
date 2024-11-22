package com.wcc.platform.domain.cms.pages;

import com.wcc.platform.domain.cms.attributes.LabelLink;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/** CMS Simple Section to be included in the pages. */
public record Section<T>(
    @NotBlank String title, String description, LabelLink link, List<T> items) {}
