package com.wcc.platform.domain.email;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Response object returned after attempting to send an email. Contains status information and
 * timestamp of the operation.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object after sending an email")
public class EmailResponse {

  @Schema(description = "Whether the email was sent successfully", example = "true")
  private boolean success;

  @Schema(description = "Status message describing the result", example = "Email sent successfully")
  private String message;

  @Schema(
      description = "Timestamp when the email was processed",
      example = "2025-11-16T10:48:50.992574288Z")
  private OffsetDateTime timestamp;

  @Schema(description = "Email address of the recipient", example = "recipient@example.com")
  private String recipient;

  @Schema(description = "Error details if the email failed to send", example = "SMTP server error")
  private String error;
}
