package com.wcc.platform.domain.exceptions;

import java.util.UUID;

/** Exception thrown when a resource is not found. */
public class ResourceNotFoundException extends RuntimeException {

  /** Constructor with resource ID. */
  public ResourceNotFoundException(final UUID id) {
    super("Resource not found with ID: " + id);
  }

  /** Constructor with a custom message. */
  public ResourceNotFoundException(final String message) {
    super(message);
  }
}
