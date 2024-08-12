package com.wcc.platform.domain.cms.pages;

/** Generic CMS Paginated Page. */
public record CmsPaginatedPage<T>(PageMetadata metadata, PageData<T> data) {}
