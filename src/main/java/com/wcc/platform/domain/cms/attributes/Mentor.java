package com.wcc.platform.domain.cms.attributes;

import jakarta.validation.constraints.NotEmpty;

/** Mentor section to be included in the Study Group card. */
public record Mentor(@NotEmpty String title, @NotEmpty String name, @NotEmpty String uri) {}
