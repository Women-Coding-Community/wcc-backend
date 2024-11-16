package com.wcc.platform.domain.cms.attributes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Record for Image CMS data. */
public record Image(@NotBlank String path, @NotBlank String alt, @NotNull ImageType type) {}
