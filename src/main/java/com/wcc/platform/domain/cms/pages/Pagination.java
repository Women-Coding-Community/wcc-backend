package com.wcc.platform.domain.cms.pages;

/** CMS Pagination attributes. */
public record Pagination(int totalItems, int totalPages, int currentPage, int pageSize) {}
