package com.wcc.platform.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.ErrorDetails;
import com.wcc.platform.domain.exceptions.ForbiddenException;
import com.wcc.platform.domain.platform.mentorship.TechnicalAreaProficiency;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  @Test
  @DisplayName(
      "Given validation errors, when handling MethodArgumentNotValidException, then return NOT_ACCEPTABLE with error messages")
  void shouldReturnNotAcceptableForMethodArgumentNotValidException() {
    var bindingResult = mock(BindingResult.class);
    var fieldError1 = new FieldError("object", "email", "must not be blank");
    var fieldError2 = new FieldError("object", "name", "size must be between 1 and 100");
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

    var exception = mock(MethodArgumentNotValidException.class);
    when(exception.getBindingResult()).thenReturn(bindingResult);

    var response =
        globalExceptionHandler.handleMethodArgumentNotValidException(exception, webRequest);

    var expectation =
        new ErrorDetails(
            BAD_REQUEST.value(),
            "email: must not be blank, name: size must be between 1 and 100",
            DETAILS);
    assertEquals(BAD_REQUEST, response.getStatusCode());
    assertEquals(expectation, response.getBody());
  }

  @Test
  void shouldReturnForbiddenForAccessDeniedException() {
    var exception = new ForbiddenException("Error");

    var response = globalExceptionHandler.handleForbiddenException(exception, webRequest);

    var expectation = new ErrorDetails(FORBIDDEN.value(), "Error", DETAILS);
    assertEquals(FORBIDDEN, response.getStatusCode());
    assertEquals(expectation, response.getBody());
  }

  @Test
  @DisplayName(
      "Given DataAccessException, when handling, then return CONFLICT with specific cause message")
  void shouldReturnConflictForDataAccessException() {
    var rootCause = new RuntimeException("duplicate key value violates unique constraint");
    var exception =
        new DuplicateKeyException("PreparedStatementCallback; SQL [INSERT...]", rootCause);

    var response = globalExceptionHandler.handleDataAccessException(exception, webRequest);

    var expectation = new ErrorDetails(CONFLICT.value(), rootCause.getMessage(), DETAILS);
    assertEquals(CONFLICT, response.getStatusCode());
    assertEquals(expectation, response.getBody());
  }

  @Test
  @DisplayName(
      "Given malformed JSON with unknown field, "
          + "when handling, then return BAD_REQUEST with field context")
  void shouldReturnBadRequestForHttpMessageNotReadableException() {
    var invalidJson =
        """
        {
          "name": "BACKEND",
          "proficiencyLevel": "BEGINNER"
        }
        """;
    var cause =
        assertThrows(
            com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException.class,
            () -> new ObjectMapper().readValue(invalidJson, TechnicalAreaProficiency.class));
    var exception =
        new HttpMessageNotReadableException(
            "JSON parse error", cause, new MockHttpInputMessage(invalidJson.getBytes()));

    var response =
        globalExceptionHandler.handleHttpMessageNotReadableException(exception, webRequest);

    var expectation =
        new ErrorDetails(
            BAD_REQUEST.value(),
            "Unrecognized field 'name' at 'name'. "
                + "Allowed fields: proficiencyLevel, technicalArea",
            DETAILS);
    assertEquals(BAD_REQUEST, response.getStatusCode());
    assertEquals(expectation, response.getBody());
  }
}
