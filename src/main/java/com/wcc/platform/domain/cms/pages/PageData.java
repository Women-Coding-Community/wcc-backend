package com.wcc.platform.domain.cms.pages;

import java.util.List;

/** Generic Page data to be returned in {@link CmsPaginatedPage}. */
public record PageData<T>(String title, String subtitle, String description, List<T> items) {}
