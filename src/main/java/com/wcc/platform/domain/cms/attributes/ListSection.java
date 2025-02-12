package com.wcc.platform.domain.cms.attributes;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/** CMS Simple ListSection to be included in the pages. */
public record ListSection<T>(
    @NotBlank String title, String description, LabelLink link, List<T> items) {}
