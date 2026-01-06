package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.email.EmailRequest;
import com.wcc.platform.domain.email.EmailResponse;
import com.wcc.platform.domain.exceptions.EmailSendException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

class EmailServiceTest {

  @Mock private JavaMailSender javaMailSender;

  @Mock private MimeMessage mimeMessage;

  @InjectMocks private EmailService emailService;

  private EmailRequest emailRequest;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(emailService, "fromEmail", "test@wcc.dev");

    emailRequest =
        EmailRequest.builder()
            .to("recipient@example.com")
            .subject("Test Subject")
            .body("Test Body")
            .html(false)
            .build();
  }

  @Test
  @DisplayName(
      "Given valid email request, when sending email, then email should be sent successfully")
  void shouldSendEmailSuccessfully() {
    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

    EmailResponse response = emailService.sendEmail(emailRequest);

    assertThat(response).isNotNull();
    assertThat(response.isSuccess()).isTrue();
    assertThat(response.getMessage()).isEqualTo("Email sent successfully");
    assertThat(response.getRecipient()).isEqualTo("recipient@example.com");
    assertThat(response.getTimestamp()).isNotNull();
    verify(javaMailSender).send(any(MimeMessage.class));
  }

  @Test
  @DisplayName(
      "Given email request with HTML content, when sending email, then email should be sent with HTML format")
  void shouldSendHtmlEmail() {
    EmailRequest htmlRequest =
        EmailRequest.builder()
            .to("recipient@example.com")
            .subject("Test Subject")
            .body("<h1>Test HTML</h1>")
            .html(true)
            .build();

    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

    EmailResponse response = emailService.sendEmail(htmlRequest);

    assertThat(response).isNotNull();
    assertThat(response.isSuccess()).isTrue();
    verify(javaMailSender).send(any(MimeMessage.class));
  }

  @Test
  @DisplayName(
      "Given email request with CC recipients, when sending email, then email should include CC recipients")
  void shouldSendEmailWithCc() {
    EmailRequest ccRequest =
        EmailRequest.builder()
            .to("recipient@example.com")
            .subject("Test Subject")
            .body("Test Body")
            .cc(List.of("cc1@example.com", "cc2@example.com"))
            .build();

    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

    EmailResponse response = emailService.sendEmail(ccRequest);

    assertThat(response).isNotNull();
    assertThat(response.isSuccess()).isTrue();
    verify(javaMailSender).send(any(MimeMessage.class));
  }

  @Test
  @DisplayName(
      "Given email request with BCC recipients, when sending email, then email should include BCC recipients")
  void shouldSendEmailWithBcc() {
    EmailRequest bccRequest =
        EmailRequest.builder()
            .to("recipient@example.com")
            .subject("Test Subject")
            .body("Test Body")
            .bcc(List.of("bcc@example.com"))
            .build();

    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

    EmailResponse response = emailService.sendEmail(bccRequest);

    assertThat(response).isNotNull();
    assertThat(response.isSuccess()).isTrue();
    verify(javaMailSender).send(any(MimeMessage.class));
  }

  @Test
  @DisplayName(
      "Given email request with reply-to address, when sending email, then email should include reply-to address")
  void shouldSendEmailWithReplyTo() {
    EmailRequest replyToRequest =
        EmailRequest.builder()
            .to("recipient@example.com")
            .subject("Test Subject")
            .body("Test Body")
            .replyTo("noreply@wcc.dev")
            .build();

    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

    EmailResponse response = emailService.sendEmail(replyToRequest);

    assertThat(response).isNotNull();
    assertThat(response.isSuccess()).isTrue();
    verify(javaMailSender).send(any(MimeMessage.class));
  }

  @Test
  @DisplayName(
      "Given mail server failure, when sending email, then should throw EmailSendException")
  void shouldThrowExceptionWhenEmailFails() {
    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    doThrow(new MailSendException("Mail server error"))
        .when(javaMailSender)
        .send(any(MimeMessage.class));

    assertThatThrownBy(() -> emailService.sendEmail(emailRequest))
        .isInstanceOf(EmailSendException.class)
        .hasMessageContaining("Failed to send email to: recipient@example.com");
  }

  @Test
  @DisplayName(
      "Given multiple email requests, when sending bulk emails, then all emails should be processed")
  void shouldSendBulkEmails() {
    EmailRequest request1 =
        EmailRequest.builder()
            .to("recipient1@example.com")
            .subject("Subject 1")
            .body("Body 1")
            .build();

    EmailRequest request2 =
        EmailRequest.builder()
            .to("recipient2@example.com")
            .subject("Subject 2")
            .body("Body 2")
            .build();

    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

    List<EmailResponse> responses = emailService.sendBulkEmails(List.of(request1, request2));

    assertThat(responses).hasSize(2);
    assertThat(responses.get(0).isSuccess()).isTrue();
    assertThat(responses.get(0).getRecipient()).isEqualTo("recipient1@example.com");
    assertThat(responses.get(1).isSuccess()).isTrue();
    assertThat(responses.get(1).getRecipient()).isEqualTo("recipient2@example.com");
  }

  @Test
  @DisplayName(
      "Given bulk emails with some failures, when sending, then should return mixed results")
  void shouldHandlePartialFailuresInBulkEmails() {
    EmailRequest request1 =
        EmailRequest.builder()
            .to("recipient1@example.com")
            .subject("Subject 1")
            .body("Body 1")
            .build();

    EmailRequest request2 =
        EmailRequest.builder()
            .to("recipient2@example.com")
            .subject("Subject 2")
            .body("Body 2")
            .build();

    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    doThrow(new MailSendException("Mail server error"))
        .doNothing()
        .when(javaMailSender)
        .send(any(MimeMessage.class));

    List<EmailResponse> responses = emailService.sendBulkEmails(List.of(request1, request2));

    assertThat(responses).hasSize(2);
    assertThat(responses.get(0).isSuccess()).isFalse();
    assertThat(responses.get(0).getError()).contains("Failed to send email");
    assertThat(responses.get(1).isSuccess()).isTrue();
  }
}
