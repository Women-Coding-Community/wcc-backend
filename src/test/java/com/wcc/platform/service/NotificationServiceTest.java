package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.configuration.NotificationTemplateConfig;
import com.wcc.platform.domain.email.EmailRequest;
import com.wcc.platform.domain.exceptions.EmailSendException;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.template.RenderedTemplate;
import com.wcc.platform.domain.template.TemplateType;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
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
  @Mock private NotificationTemplateConfig notificationTemplateConfig;
  private Mentor mentor;

  @InjectMocks private NotificationService notificationService;

  @BeforeEach
  void setUp() {
    mentor = createMentorTest();
  }

  @Test
  @DisplayName(
      "Given recipient, template type and params, when sendNotification,"
          + " then renders template and sends email")
  void sendNotificationRendersTemplateAndSendsEmail() {
    var websiteLink = "https://www.womencodingcommunity.com/";
    var mentorProfilePath = websiteLink + "mentors/";
    var recipient = mentor.getEmail();
    var mentorName = mentor.getFullName();
    var templateType = TemplateType.MENTOR_APPROVAL;
    var expectedBody =
        "<p>Dear "
            + mentorName
            + ",</p>"
            + "<p>Your profile is available here "
            + mentorProfilePath
            + mentor.getId()
            + ".</p>";
    var rendered = new RenderedTemplate("WCC: Mentor Profile Approval Confirmation", expectedBody);

    when(notificationTemplateConfig.getWebsiteLink()).thenReturn(websiteLink);
    when(notificationTemplateConfig.getMentorLinkBase()).thenReturn("mentors/");

    when(emailTemplateService.renderTemplate(eq(templateType), any(Map.class)))
        .thenReturn(rendered);

    notificationService.sendMentorApprovalEmail(mentor);

    verify(emailTemplateService).renderTemplate(eq(templateType), any(Map.class));

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
    when(notificationTemplateConfig.getWebsiteLink())
        .thenReturn("https://www.womencodingcommunity.com/");
    when(notificationTemplateConfig.getMentorLinkBase()).thenReturn("mentors/");
    when(emailTemplateService.renderTemplate(any(), any()))
        .thenThrow(new EmailSendException("Template not found"));

    notificationService.sendMentorApprovalEmail(mentor);

    verify(emailService, never()).sendEmail(any());
  }
}
