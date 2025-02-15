package com.wcc.platform.domain.cms.attributes;

import jakarta.validation.constraints.NotNull;

/**
 * Represents a FAQ item with a question and answer.
 *
 * @param question the question of the FAQ item
 * @param answer the answer of the FAQ item
 */
public record FaqItem(@NotNull String question, @NotNull String answer) {}
