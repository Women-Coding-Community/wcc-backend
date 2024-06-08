package com.wcc.platform.domain.cms.pages;

public record Pagination(int totalItems, int totalPages, int currentPage, int pageSize) {
}
