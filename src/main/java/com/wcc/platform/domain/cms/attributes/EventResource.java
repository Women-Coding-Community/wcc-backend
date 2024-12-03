package com.wcc.platform.domain.cms.attributes;

import jakarta.validation.constraints.NotNull;

/**
 * Record for an external link.
 *
 * @param link simple link of resources.
 */
public record EventResource(@NotNull LabelLink link) {}
