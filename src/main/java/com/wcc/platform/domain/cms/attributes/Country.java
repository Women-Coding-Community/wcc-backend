package com.wcc.platform.domain.cms.attributes;

import jakarta.validation.constraints.NotBlank;

/** Record for Country CMS data. */
public record Country(@NotBlank String countryCode, @NotBlank String countryName) {}
