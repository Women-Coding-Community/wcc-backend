package com.wcc.platform.domain.cms.attributes;

import jakarta.validation.constraints.NotNull;

/**
 * Represents a FAQ item with a title and description.
 *
 * @param title the title of the FAQ item
 * @param description the description of the FAQ item
 */
public record FaqItem(@NotNull String title, @NotNull String description) {}
