package com.wcc.platform.domain.email;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request object for sending emails through the email service. Contains all the necessary
 * information for composing and delivering an email message.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for sending emails")
public class EmailRequest {

  @Schema(description = "BCC recipients", example = "[\"bcc@example.com\"]")
  @NotEmpty(message = "Recipient emails are required")
  private List<String> recipients = List.of();

  @NotBlank(message = "Subject is required")
  @Size(max = 255, message = "Subject must not exceed 255 characters")
  @Schema(description = "Email subject", example = "Welcome to WCC!")
  private String subject;

  @NotBlank(message = "Body is required")
  @Schema(
      description = "Email body content (can be plain text or HTML)",
      example = "Hello, welcome to Women Coding Community!")
  private String body;

  @Schema(
      description = "Whether the email body contains HTML content",
      example = "false",
      defaultValue = "true")
  private boolean html = true;

  @Schema(description = "Reply-to email address", example = "noreply@womencodingcommunity.com")
  private String replyTo;
}
