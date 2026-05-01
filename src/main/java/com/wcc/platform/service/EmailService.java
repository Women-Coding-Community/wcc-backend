package com.wcc.platform.service;

import com.wcc.platform.domain.email.EmailRequest;
import com.wcc.platform.domain.email.EmailResponse;
import com.wcc.platform.domain.email.TemplateEmailRequest;
import com.wcc.platform.domain.exceptions.EmailSendException;
import com.wcc.platform.domain.template.RenderedTemplate;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * Service class for handling email operations. Provides functionality to send emails using
 * configured SMTP server settings.
 */
@Slf4j
@Service
public class EmailService {

  private final JavaMailSender javaMailSender;

  private final String fromEmail;
  private final EmailTemplateService emailTemplateService;

  /** Constructor for EmailService. */
  @Autowired
  public EmailService(
      final JavaMailSender javaMailSender,
      final @Value("${spring.mail.username}") String fromEmail,
      final EmailTemplateService emailTemplateService) {
    this.javaMailSender = javaMailSender;
    this.fromEmail = fromEmail;
    this.emailTemplateService = emailTemplateService;
  }

  /**
   * Sends an email based on the provided email request always sending as BCC.
   *
   * @param emailRequest the email request containing recipient, subject, body, and other details
   * @return EmailResponse containing the status of the email sending operation
   * @throws EmailSendException if the email fails to send
   */
  public EmailResponse sendEmail(final EmailRequest emailRequest) {
    try {
      log.info("Attempting to send email to: {}", emailRequest.getRecipients());

      final MimeMessage message = javaMailSender.createMimeMessage();
      final var mimeMessageHelper =
          new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

      mimeMessageHelper.setFrom(fromEmail);
      mimeMessageHelper.setSubject(emailRequest.getSubject());
      mimeMessageHelper.setText(emailRequest.getBody(), emailRequest.isHtml());

      mimeMessageHelper.setBcc(emailRequest.getRecipients().toArray(new String[0]));

      if (!ObjectUtils.isEmpty(emailRequest.getReplyTo())) {
        mimeMessageHelper.setReplyTo(emailRequest.getReplyTo());
      }

      javaMailSender.send(message);

      log.info("Email sent successfully to: {}", emailRequest.getRecipients());

      return EmailResponse.builder()
          .success(true)
          .message("Email sent successfully")
          .timestamp(OffsetDateTime.now())
          .recipient(String.join(";", emailRequest.getRecipients()))
          .build();
    } catch (MessagingException e) {
      log.error(
          "Failed to send email to: {}. Error: {}",
          emailRequest.getRecipients(),
          e.getMessage(),
          e);
      throw new EmailSendException("Failed to send email to: " + emailRequest.getRecipients(), e);
    }
  }

  /**
   * Sends an email using a pre-defined template with dynamic parameters.
   *
   * @param request the template email request containing recipient, template type, parameters and
   *     optional CC, BCC and reply-to fields
   * @return an {@link EmailResponse} indicating whether the email was sent successfully
   */
  public EmailResponse sendTemplateEmail(final TemplateEmailRequest request) {
    final RenderedTemplate renderedTemplate =
        emailTemplateService.renderTemplate(
            request.getTemplateType(), request.getTemplateParameters());

    final var emailBuilder =
        EmailRequest.builder()
            .subject(renderedTemplate.subject())
            .body(renderedTemplate.body())
            .html(request.isHtml());

    if (CollectionUtils.isEmpty(request.getBcc())) {
      emailBuilder.recipients(List.of(request.getTo()));
    } else {
      final var recipients = new java.util.ArrayList<String>();
      recipients.add(request.getTo());
      recipients.addAll(request.getBcc());
      emailBuilder.recipients(recipients);
    }

    if (StringUtils.isNotBlank(request.getReplyTo())) {
      emailBuilder.replyTo(request.getReplyTo());
    }

    return sendEmail(emailBuilder.build());
  }

  /**
   * Sends multiple emails in bulk.
   *
   * @param emailRequests list of email requests to send
   * @return list of EmailResponse objects containing the status of each email
   */
  public List<EmailResponse> sendBulkEmails(final List<EmailRequest> emailRequests) {
    log.info("Sending bulk emails. Total count: {}", emailRequests.size());

    return emailRequests.stream()
        .map(
            request -> {
              try {
                return sendEmail(request);
              } catch (EmailSendException e) {
                log.error("Failed to send bulk email to: {}", request.getRecipients(), e);
                return EmailResponse.builder()
                    .success(false)
                    .message("Failed to send email")
                    .timestamp(OffsetDateTime.now())
                    .recipient(String.join(";", request.getRecipients()))
                    .error(e.getMessage())
                    .build();
              }
            })
        .toList();
  }
}
