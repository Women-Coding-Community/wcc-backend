package com.wcc.platform.controller;

import com.wcc.platform.domain.email.EmailRequest;
import com.wcc.platform.domain.email.EmailResponse;
import com.wcc.platform.domain.template.RenderedTemplate;
import com.wcc.platform.domain.template.TemplateRequest;
import com.wcc.platform.domain.template.TemplateType;
import com.wcc.platform.service.EmailService;
import com.wcc.platform.service.EmailTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for email service APIs. */
@RestController
@RequestMapping("/api/platform/v1/email")
@SecurityRequirement(name = "apiKey")
@Tag(name = "Platform: Emails", description = "Email APIs for sending emails and templates")
@RequiredArgsConstructor
public class EmailController {

  private final EmailService emailService;
  private final EmailTemplateService emailTemplateService;

  /**
   * API to send a single email.
   *
   * @param emailRequest the email request containing recipient, subject, and body
   * @return EmailResponse with the status of the email sending operation
   */
  @PostMapping("/send")
  @Operation(
      summary = "Send a single email",
      description = "Sends an email to the specified recipient")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Email sent successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EmailResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid email request", content = @Content),
    @ApiResponse(responseCode = "500", description = "Failed to send email", content = @Content)
  })
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<EmailResponse> sendEmail(
      @Parameter(description = "Email Template Type, for example: FEEDBACK_MENTOR_ADHOC", required = true)
      @RequestParam(name = "emailTemplate")
      final TemplateType emailTemplateType,
      @Valid @RequestBody final EmailRequest emailRequest) {
    final EmailResponse response = emailService.sendEmail(emailRequest);
    return ResponseEntity.ok(response);
  }

  /**
   * API to send multiple emails in bulk.
   *
   * @param emailRequests list of email requests to send
   * @return list of EmailResponse objects with the status of each email
   */
  @PostMapping("/send/bulk")
  @Operation(
      summary = "Send multiple emails in bulk",
      description = "Sends multiple emails to different recipients")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Bulk emails processed",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EmailResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid email requests", content = @Content)
  })
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<EmailResponse>> sendBulkEmails(
      @Valid @RequestBody final List<EmailRequest> emailRequests) {
    final List<EmailResponse> responses = emailService.sendBulkEmails(emailRequests);
    return ResponseEntity.ok(responses);
  }

  /**
   * API to preview an email template.
   *
   * @param templateRequest the template request containing template type and parameters
   * @return RenderedTemplate with the subject and body of the rendered template
   */
  @PostMapping("/template/preview")
  @Operation(summary = "Preview an email template", description = "Renders an email template")
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Template rendered successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RenderedTemplate.class))),
    @ApiResponse(responseCode = "400", description = "Invalid template request", content = @Content)
  })
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<RenderedTemplate> previewTemplate(
      @Valid @RequestBody final TemplateRequest templateRequest) {
    final RenderedTemplate renderedTemplate =
        emailTemplateService.renderTemplate(
            templateRequest.templateType(), templateRequest.params());
    return new ResponseEntity<>(renderedTemplate, HttpStatus.CREATED);
  }
}
