package com.wcc.platform.domain.cms.attributes;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/** CMS Page Section which allows to listed related topics. */
public record PageSection(
    @NotBlank String title, String description, LabelLink link, List<String> topics) {}
