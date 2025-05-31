package com.wcc.platform.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.CONFLICT;

import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.ErrorDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.WebRequest;

class GlobalExceptionHandlerTest {

  private static final String DETAILS = "Test description";
  private GlobalExceptionHandler globalExceptionHandler;
  private WebRequest webRequest;

  @BeforeEach
  void setUp() {
    globalExceptionHandler = new GlobalExceptionHandler();
    webRequest = mock(WebRequest.class);
    when(webRequest.getDescription(false)).thenReturn(DETAILS);
  }

  @Test
  void givenHandleDuplicatedRecordWhenHandleDuplicatedRecordThenConsiderConflict() {
    var exception = new DuplicatedMemberException("Error");

    var response = globalExceptionHandler.handleRecordAlreadyExitsException(exception, webRequest);

    var expectation = new ErrorDetails(CONFLICT.value(), "Record already exists: Error", DETAILS);
    assertEquals(CONFLICT, response.getStatusCode());
    assertEquals(expectation, response.getBody());
  }
}
