package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.baseBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.configuration.NotificationConfig;
import com.wcc.platform.domain.email.EmailRequest;
import com.wcc.platform.domain.exceptions.EmailSendException;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MatchStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipMatch;
import com.wcc.platform.domain.template.RenderedTemplate;
import com.wcc.platform.domain.template.TemplateType;
import com.wcc.platform.repository.MemberRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorshipNotificationServiceTest {

  @Mock private EmailTemplateService emailTemplateService;
  @Mock private MemberRepository memberRepository;
  @Mock private EmailService emailService;
  @Mock private NotificationConfig notificationConfig;
  private Mentor mentor;

  @InjectMocks private MentorshipNotificationService notificationService;

  @BeforeEach
  void setUp() {
    mentor = createMentorTest();
  }

  @Test
  @DisplayName(
      "Given recipient, template type and params, when sendNotification,"
          + " then renders template and sends email")
  void sendNotificationRendersTemplateAndSendsEmail() {
    var mentorProfilePath = "https://www.womencodingcommunity.com/mentors?keywords=Jane+Doe";
    var recipient = mentor.getEmail();
    var mentorName = mentor.getFullName();
    var templateType = TemplateType.MENTOR_APPROVED;
    var expectedBody =
        "<p>Dear "
            + mentorName
            + ",</p>"
            + "<p>Your profile is available here "
            + mentorProfilePath
            + mentorName
            + ".</p>";
    var rendered = new RenderedTemplate("Mentor Profile Approval Confirmation", expectedBody);

    when(notificationConfig.getMentorProfileUrl()).thenReturn(mentorProfilePath);

    when(emailTemplateService.renderTemplate(eq(templateType), any(Map.class)))
        .thenReturn(rendered);

    notificationService.sendMentorApprovalEmail(mentor);

    verify(emailTemplateService).renderTemplate(eq(templateType), any(Map.class));

    ArgumentCaptor<EmailRequest> emailCaptor = ArgumentCaptor.forClass(EmailRequest.class);
    verify(emailService).sendEmail(emailCaptor.capture());

    var emailRequest = emailCaptor.getValue();
    assertThat(emailRequest.getRecipients()).contains(recipient);
    assertThat(emailRequest.getSubject()).isEqualTo(rendered.subject());
    assertThat(emailRequest.getBody()).isEqualTo(rendered.body());
    assertThat(emailRequest.isHtml()).isFalse();
  }

  @Test
  @DisplayName(
      "Given renderTemplate throws, when sendNotification,"
          + " then exception is caught and sendEmail is not called")
  void sendNotificationWhenRenderFailsThenDoesNotSendEmail() {
    when(notificationConfig.getMentorProfileUrl())
        .thenReturn("https://www.womencodingcommunity.com/mentors?keywords=Jane+Doe");
    when(emailTemplateService.renderTemplate(any(), any()))
        .thenThrow(new EmailSendException("Template not found"));

    notificationService.sendMentorApprovalEmail(mentor);

    verify(emailService, never()).sendEmail(any());
  }

  @Test
  @DisplayName(
      "Given valid application update, when sendApplicationUpdate, then sends notification")
  void shouldSendApplicationUpdateNotification() {
    var application =
        Optional.of(baseBuilder(1L, 10L, 20L, 5).status(ApplicationStatus.PENDING).build());
    var updated = baseBuilder(1L, 10L, 20L, 5).status(ApplicationStatus.MENTOR_REVIEWING).build();

    when(memberRepository.findEmails(anyList()))
        .thenReturn(List.of("mentor@test.com", "mentee@test.com"));
    when(notificationConfig.getMentorshipEmail()).thenReturn("team@test.com");
    when(emailTemplateService.renderTemplate(any(), any()))
        .thenReturn(new RenderedTemplate("Subject", "Body"));

    notificationService.sendApplicationUpdate(application, updated);

    verify(emailTemplateService).renderTemplate(eq(TemplateType.MENTEE_APPLICATIONS), anyMap());
    verify(emailService).sendEmail(any());
  }

  @Test
  @DisplayName("Given valid match update, when sendMatchUpdate, then sends notification")
  void shouldSendMatchUpdateNotification() {
    var match =
        MentorshipMatch.builder()
            .matchId(1L)
            .mentorId(20L)
            .menteeId(10L)
            .cycleId(5L)
            .status(MatchStatus.ACTIVE)
            .startDate(java.time.LocalDate.now())
            .build();

    when(memberRepository.findEmails(anyList()))
        .thenReturn(List.of("mentor@test.com", "mentee@test.com"));
    when(notificationConfig.getMentorshipEmail()).thenReturn("team@test.com");
    when(emailTemplateService.renderTemplate(any(), any()))
        .thenReturn(new RenderedTemplate("Subject", "Body"));

    notificationService.sendMatchUpdate(Optional.empty(), match);

    verify(emailTemplateService).renderTemplate(eq(TemplateType.MATCH_APPLICATIONS), anyMap());
    verify(emailService).sendEmail(any());
  }
}
