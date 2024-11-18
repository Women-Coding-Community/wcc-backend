package com.wcc.platform.domain.cms.pages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Generic Page data to be returned in {@link CmsPaginatedPage}. */
public record PageData<T>(
    @NotBlank String title, String subtitle, String description, @NotEmpty List<T> items) {}
