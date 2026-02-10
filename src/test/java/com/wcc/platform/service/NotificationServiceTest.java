package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.email.EmailRequest;
import com.wcc.platform.domain.template.RenderedTemplate;
import com.wcc.platform.domain.template.TemplateType;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock private EmailTemplateService emailTemplateService;
  @Mock private EmailService emailService;

  @InjectMocks private NotificationService notificationService;

  @Test
  @DisplayName(
      "Given recipient, template type and params, when sendNotification,"
          + " then renders template and sends email")
  void sendNotificationRendersTemplateAndSendsEmail() {
    String recipient = "mentor@example.com";
    TemplateType templateType = TemplateType.MENTOR_APPROVAL;
    Map<String, String> params = Map.of("mentorName", "Jane");
    RenderedTemplate rendered =
        new RenderedTemplate("You are approved", "<p>Hello Jane, you are approved.</p>");

    when(emailTemplateService.renderTemplate(templateType, params)).thenReturn(rendered);

    notificationService.sendNotification(recipient, templateType, params);

    verify(emailTemplateService).renderTemplate(eq(templateType), eq(params));

    ArgumentCaptor<EmailRequest> emailCaptor = ArgumentCaptor.forClass(EmailRequest.class);
    verify(emailService).sendEmail(emailCaptor.capture());

    var emailRequest = emailCaptor.getValue();
    assertThat(emailRequest.getTo()).isEqualTo(recipient);
    assertThat(emailRequest.getSubject()).isEqualTo(rendered.subject());
    assertThat(emailRequest.getBody()).isEqualTo(rendered.body());
    assertThat(emailRequest.isHtml()).isTrue();
  }

  @Test
  @DisplayName(
      "Given renderTemplate throws, when sendNotification,"
          + " then exception is caught and sendEmail is not called")
  void sendNotificationWhenRenderFailsThenDoesNotSendEmail() {
    when(emailTemplateService.renderTemplate(any(), any()))
        .thenThrow(new RuntimeException("Template not found"));

    notificationService.sendNotification(
        "mentor@example.com", TemplateType.MENTOR_APPROVAL, Map.of("mentorName", "Jane"));

    verify(emailService, never()).sendEmail(any());
  }
}
