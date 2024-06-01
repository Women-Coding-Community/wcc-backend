package com.wcc.platform.domain.pages;

public record Pagination(int totalItems, int totalPages, int currentPage, int pageSize) {
}
