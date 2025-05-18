package com.wcc.platform.domain.cms.attributes;

import jakarta.validation.constraints.NotEmpty;

/** Record for the number of participants in a study group. */
public record Participants(@NotEmpty String title, @NotEmpty int number) {}
