package com.wcc.platform.service;

import com.wcc.platform.domain.email.EmailRequest;
import com.wcc.platform.domain.email.EmailResponse;
import com.wcc.platform.domain.exceptions.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
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

  @Autowired
  public EmailService(
      final JavaMailSender javaMailSender,
      final @Value("${spring.mail.username}") String fromEmail) {
    this.javaMailSender = javaMailSender;
    this.fromEmail = fromEmail;
  }

  /**
   * Sends an email based on the provided email request.
   *
   * @param emailRequest the email request containing recipient, subject, body, and other details
   * @return EmailResponse containing the status of the email sending operation
   * @throws EmailSendException if the email fails to send
   */
  public EmailResponse sendEmail(final EmailRequest emailRequest) {
    try {
      log.info("Attempting to send email to: {}", emailRequest.getTo());

      final MimeMessage message = javaMailSender.createMimeMessage();
      final MimeMessageHelper helper =
          new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

      helper.setFrom(fromEmail);
      helper.setTo(emailRequest.getTo());
      helper.setSubject(emailRequest.getSubject());
      helper.setText(emailRequest.getBody(), emailRequest.isHtml());

      if (!ObjectUtils.isEmpty(emailRequest.getCc())) {
        helper.setCc(emailRequest.getCc().toArray(new String[0]));
      }

      if (!ObjectUtils.isEmpty(emailRequest.getBcc())) {
        helper.setBcc(emailRequest.getBcc().toArray(new String[0]));
      }

      if (!ObjectUtils.isEmpty(emailRequest.getReplyTo())) {
        helper.setReplyTo(emailRequest.getReplyTo());
      }

      javaMailSender.send(message);

      log.info("Email sent successfully to: {}", emailRequest.getTo());

      return EmailResponse.builder()
          .success(true)
          .message("Email sent successfully")
          .timestamp(OffsetDateTime.now())
          .recipient(emailRequest.getTo())
          .build();

    } catch (MessagingException | MailException e) {
      log.error("Failed to send email to: {}. Error: {}", emailRequest.getTo(), e.getMessage(), e);
      throw new EmailSendException("Failed to send email to: " + emailRequest.getTo(), e);
    }
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
                log.error("Failed to send bulk email to: {}", request.getTo(), e);
                return EmailResponse.builder()
                    .success(false)
                    .message("Failed to send email")
                    .timestamp(OffsetDateTime.now())
                    .recipient(request.getTo())
                    .error(e.getMessage())
                    .build();
              }
            })
        .toList();
  }
}
