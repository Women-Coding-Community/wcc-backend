package com.wcc.platform.domain.exceptions;

/** CMS error details to give more context when some exception is trigger. */
public record ErrorDetails(int status, String message, String details) {}
