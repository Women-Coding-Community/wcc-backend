package com.wcc.platform.configuration;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.ErrorDetails;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/** Global controller to handle all exceptions for the API. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Receive ContentNotFoundException and return {@link HttpStatus#NOT_FOUND}. */
  @ExceptionHandler(ContentNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<?> handleNotFoundException(
      ContentNotFoundException ex, WebRequest request) {
    var errorDetails =
        new ErrorDetails(NOT_FOUND.value(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, NOT_FOUND);
  }

  /** Receive PlatformInternalException and return {@link HttpStatus#INTERNAL_SERVER_ERROR}. */
  @ExceptionHandler(PlatformInternalException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<?> handleInternalError(PlatformInternalException ex, WebRequest request) {
    var errorDetails =
        new ErrorDetails(
            INTERNAL_SERVER_ERROR.value(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);
  }
}
