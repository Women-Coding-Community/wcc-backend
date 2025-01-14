package com.wcc.platform.domain.cms.pages;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * CMS Code of conduct page.
 *
 * @param page Page details as title and description
 * @param items all details
 */
public record CodeOfConductPage(
    @NotNull String id, @NotNull Page page, @NotEmpty List<Section<String>> items) {}
