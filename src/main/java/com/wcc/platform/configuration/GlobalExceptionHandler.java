package com.wcc.platform.configuration;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.wcc.platform.domain.exceptions.*;
import com.wcc.platform.repository.file.FileRepositoryException;
import jakarta.validation.ConstraintViolationException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/** Global controller to handle all exceptions for the API. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Receive ContentNotFoundException and return {@link HttpStatus#NOT_FOUND}. */
  @ExceptionHandler({
    ContentNotFoundException.class,
    NoSuchElementException.class,
    MemberNotFoundException.class,
    MentorNotFoundException.class,
    ApplicationNotFoundException.class
  })
  @ResponseStatus(NOT_FOUND)
  public ResponseEntity<ErrorDetails> handleNotFoundException(
      final RuntimeException ex, final WebRequest request) {
    final var errorDetails =
        new ErrorDetails(NOT_FOUND.value(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, NOT_FOUND);
  }

  /** Receive PlatformInternalException and return {@link HttpStatus#INTERNAL_SERVER_ERROR}. */
  @ExceptionHandler({
    PlatformInternalException.class,
    FileRepositoryException.class,
    EmailSendException.class,
    MenteeNotSavedException.class
  })
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorDetails> handleInternalError(
      final RuntimeException ex, final WebRequest request) {
    final var errorDetails =
        new ErrorDetails(
            INTERNAL_SERVER_ERROR.value(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);
  }

  /**
   * Receive {@link InvalidProgramTypeException} or {@link IllegalArgumentException} then return
   * {@link HttpStatus#BAD_REQUEST}.
   */
  @ExceptionHandler({
    InvalidProgramTypeException.class,
    IllegalArgumentException.class,
    TemplateValidationException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDetails> handleProgramTypeError(
      final RuntimeException ex, final WebRequest request) {
    final var errorDetails =
        new ErrorDetails(
            HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  /** Receive {@link DataIntegrityViolationException} then return {@link HttpStatus#CONFLICT}. */
  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ErrorDetails> handleDataAccessException(
      final DataIntegrityViolationException ex, final WebRequest request) {
    final var errorDetails =
        new ErrorDetails(
            HttpStatus.CONFLICT.value(),
            ex.getMostSpecificCause().getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
  }

  /** Receive {@link DuplicatedException} subclasses and return {@link HttpStatus#CONFLICT}. */
  @ExceptionHandler(DuplicatedException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ErrorDetails> handleRecordAlreadyExitsException(
      final RuntimeException ex, final WebRequest request) {
    final var errorDetails =
        new ErrorDetails(
            HttpStatus.CONFLICT.value(),
            "Record already exists: " + ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
  }

  /** Receive MentorStatusException and return {@link HttpStatus#CONFLICT}. */
  @ExceptionHandler(MentorStatusException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ErrorDetails> handleMentorStatus(
      final MentorStatusException ex, final WebRequest request) {
    final var errorDetails =
        new ErrorDetails(
            HttpStatus.CONFLICT.value(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
  }

  /** Receive Constraints violations and return {@link HttpStatus#NOT_ACCEPTABLE}. */
  @ExceptionHandler({
    ApplicationMenteeWorkflowException.class,
    ConstraintViolationException.class,
    MentorshipCycleClosedException.class,
    MenteeRegistrationLimitException.class
  })
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  public ResponseEntity<ErrorDetails> handleNotAcceptableError(
      final RuntimeException ex, final WebRequest request) {
    final var errorDetails =
        new ErrorDetails(
            HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_ACCEPTABLE);
  }

  /**
   * Receive {@link MethodArgumentNotValidException} for bean validation errors and return {@link
   * HttpStatus#NOT_ACCEPTABLE}.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDetails> handleMethodArgumentNotValidException(
      final MethodArgumentNotValidException ex, final WebRequest request) {
    final var errorMessage =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
    final var errorDetails =
        new ErrorDetails(
            HttpStatus.BAD_REQUEST.value(), errorMessage, request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  /** Return 400 Bad Request for malformed JSON payloads. */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDetails> handleHttpMessageNotReadableException(
      final HttpMessageNotReadableException ex, final WebRequest request) {
    final var errorDetails =
        new ErrorDetails(
            HttpStatus.BAD_REQUEST.value(),
            extractReadableMessage(ex),
            request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  /** Return 403 Forbidden for ForbiddenException. */
  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorDetails> handleForbiddenException(
      final ForbiddenException ex, final WebRequest request) {
    final var errorResponse =
        new ErrorDetails(
            HttpStatus.FORBIDDEN.value(), ex.getMessage(), request.getDescription(false));

    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  private String extractReadableMessage(final HttpMessageNotReadableException ex) {
    final var cause = ex.getMostSpecificCause();

    if (cause instanceof UnrecognizedPropertyException unrecognizedProperty) {
      final var allowedFields =
          unrecognizedProperty.getKnownPropertyIds().stream()
              .map(String::valueOf)
              .sorted()
              .collect(Collectors.joining(", "));

      return "Unrecognized field '%s' at '%s'. Allowed fields: %s"
          .formatted(
              unrecognizedProperty.getPropertyName(),
              formatPath(unrecognizedProperty.getPath()),
              allowedFields);
    }

    return cause.getMessage();
  }

  private String formatPath(final java.util.List<JsonMappingException.Reference> path) {
    if (path.isEmpty()) {
      return "$";
    }

    return IntStream.range(0, path.size())
        .mapToObj(index -> formatPathReference(path.get(index), index == 0))
        .collect(Collectors.joining());
  }

  private String formatPathReference(
      final JsonMappingException.Reference reference, final boolean firstReference) {
    if (reference.getFieldName() != null) {
      return firstReference ? reference.getFieldName() : "." + reference.getFieldName();
    }

    if (reference.getIndex() >= 0) {
      return "[" + reference.getIndex() + "]";
    }

    return firstReference ? "$" : "";
  }
}
