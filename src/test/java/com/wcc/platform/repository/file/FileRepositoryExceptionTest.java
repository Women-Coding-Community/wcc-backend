package com.wcc.platform.repository.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FileRepositoryExceptionTest {

  @Test
  void testExceptionMessageAndCause() {
    String message = "Test exception message";
    Throwable cause = new RuntimeException("Root cause");

    FileRepositoryException exception = new FileRepositoryException(message, cause);

    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }
}
