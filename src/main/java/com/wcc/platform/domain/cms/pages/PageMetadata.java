package com.wcc.platform.domain.cms.pages;

import jakarta.validation.constraints.NotNull;

/** CMS Page metadata attributes to be used to debug and to apply pagination. */
public record PageMetadata(@NotNull Pagination pagination) {}
