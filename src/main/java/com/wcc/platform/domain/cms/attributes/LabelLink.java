package com.wcc.platform.domain.cms.attributes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;

/** Web link attributes to be shown in the frontend. */
public record LabelLink(
    @JsonInclude(Include.NON_NULL) String title,
    @JsonInclude(Include.NON_NULL) String label,
    @NotBlank String uri) {}
