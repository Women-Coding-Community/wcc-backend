package com.wcc.platform.domain.cms.pages;

import java.util.List;

/** Generic Page data to be returned. */
public record PageData<T>(List<T> items) {}
