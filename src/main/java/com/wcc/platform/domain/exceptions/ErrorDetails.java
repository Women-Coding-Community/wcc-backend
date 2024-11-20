package com.wcc.platform.domain.exceptions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** CMS error details to give more context when some exception is trigger. */
public record ErrorDetails(@NotNull int status, @NotBlank String message, String details) {}
