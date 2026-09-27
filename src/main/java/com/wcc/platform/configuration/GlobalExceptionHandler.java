package com.wcc.platform.configuration;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.wcc.platform.domain.exceptions.*;
import com.wcc.platform.repository.file.FileRepositoryException;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/** Global controller to handle all exceptions for the API. */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Receive ContentNotFoundException and return {@link HttpStatus#NOT_FOUND}. */
  @ExceptionHandler({
    ContentNotFoundException.class,
    NoSuchElementException.class,
    MemberNotFoundException.class,
    MentorNotFoundException.class,
    ApplicationNotFoundException.class,
    ResourceNotFoundException.class
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
    log.error("Internal error: {}", ex.getMessage(), ex);
    final var errorDetails =
        new ErrorDetails(
            INTERNAL_SERVER_ERROR.value(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);
  }

  /**
   * @param ex
   * @param request
   * @return
   */
  @ExceptionHandler({
    InvalidProgramTypeException.class,
    IllegalArgumentException.class,
    TemplateValidationException.class,
    InvalidTokenException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorDetails> handleBadRequest(
      final RuntimeException ex, final WebRequest request) {
    final var errorDetails =
        new ErrorDetails(
            HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({
    MentorStatusException.class,
    MentorCapacityExceededException.class,
    InvalidCycleStatusTransitionException.class,
    DataIntegrityViolationException.class,
    DuplicatedException.class
  })
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ErrorDetails> handleConflicts(
      final Exception ex, final WebRequest request) {
    String message = ex.getMessage();
    if (ex instanceof DataIntegrityViolationException dive) {
      message = dive.getMostSpecificCause().getMessage();
    } else if (ex instanceof DuplicatedException) {
      message = "Record already exists: " + message;
    }

    final var errorDetails =
        new ErrorDetails(HttpStatus.CONFLICT.value(), message, request.getDescription(false));
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

  /** Return 400 Bad Request for MissingServletRequestPartException. */
  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<ErrorDetails> handleMissingServletRequestPartException(
      final MissingServletRequestPartException ex, final WebRequest request) {
    log.warn("Missing servlet request part error: {}", ex.getMessage(), ex);
    final var errorDetails =
        new ErrorDetails(
            HttpStatus.BAD_REQUEST.value(),
            "Required part '"
                + ex.getRequestPartName()
                + "' is not present. Please select a file to upload.",
            request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  /** Return 413 Payload Too Large or 400 Bad Request for MultipartException. */
  @ExceptionHandler({MaxUploadSizeExceededException.class, MultipartException.class})
  public ResponseEntity<ErrorDetails> handleMultipartException(
      final Exception ex, final WebRequest request) {
    log.warn("Multipart request error: {}", ex.getMessage(), ex);
    Throwable root = ex;
    while (root.getCause() != null && !root.equals(root.getCause())) {
      root = root.getCause();
    }
    final String rootName = root.getClass().getSimpleName();
    final String rootMsg =
        root.getMessage() != null ? root.getMessage().toLowerCase(Locale.ROOT) : "";
    final String exMsg = ex.getMessage() != null ? ex.getMessage().toLowerCase(Locale.ROOT) : "";
    final boolean isSizeLimit =
        ex instanceof MaxUploadSizeExceededException
            || rootName.contains("SizeLimitExceeded")
            || rootName.contains("FileSizeLimitExceeded")
            || rootName.contains("SizeException")
            || exMsg.contains("size limit")
            || exMsg.contains("maximum upload size")
            || rootMsg.contains("size limit")
            || rootMsg.contains("exceeds the configured maximum")
            || rootMsg.contains("maximum upload size")
            || rootMsg.contains("eofexception");

    if (isSizeLimit) {
      final var errorDetails =
          new ErrorDetails(
              HttpStatus.PAYLOAD_TOO_LARGE.value(),
              "Uploaded file exceeds the maximum allowed upload limit of 50MB",
              request.getDescription(false));
      return new ResponseEntity<>(errorDetails, HttpStatus.PAYLOAD_TOO_LARGE);
    }
    final var errorDetails =
        new ErrorDetails(
            HttpStatus.BAD_REQUEST.value(),
            "Failed to process multipart upload request: " + ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
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

  private String formatPath(final List<Reference> path) {
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
