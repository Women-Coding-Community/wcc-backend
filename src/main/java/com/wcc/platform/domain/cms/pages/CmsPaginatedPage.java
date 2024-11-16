package com.wcc.platform.domain.cms.pages;

import jakarta.validation.constraints.NotNull;

/** Generic CMS Paginated Page. */
public record CmsPaginatedPage<T>(@NotNull PageMetadata metadata, @NotNull PageData<T> data) {}
