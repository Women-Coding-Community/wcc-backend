package com.wcc.platform.service;

import com.wcc.platform.configuration.NotificationConfig;
import com.wcc.platform.domain.email.EmailRequest;
import com.wcc.platform.domain.exceptions.EmailSendException;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.template.TemplateType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipNotificationService {
  private final EmailTemplateService emailTemplateService;
  private final EmailService emailService;
  private final NotificationConfig notificationConfig;

  /**
   * Sends a mentor approval notification email to the specified mentor.
   *
   * @param mentor the mentor to notify
   */
  public void sendMentorApprovalEmail(final Mentor mentor) {
    final String mentorBaseUrl =
        notificationConfig.getMentorProfileUrl()
            + URLEncoder.encode(mentor.getFullName(), StandardCharsets.UTF_8);

    sendNotification(
        mentor.getEmail(),
        TemplateType.MENTOR_APPROVED,
        Map.of("mentorName", mentor.getFullName(), "mentorLink", mentorBaseUrl));
  }

  /**
   * Renders an email template and sends a notification email to the specified recipient.
   *
   * @param recipientEmail the recipient's email address
   * @param templateType the type of template to render
   * @param templateParams the parameters to use for rendering the template
   */
  private void sendNotification(
      final String recipientEmail,
      final TemplateType templateType,
      final Map<String, String> templateParams) {
    try {
      final var template = emailTemplateService.renderTemplate(templateType, templateParams);

      final var emailRequest =
          EmailRequest.builder()
              .to(recipientEmail)
              .subject(template.subject())
              .body(template.body())
              .html(true)
              .build();

      emailService.sendEmail(emailRequest);
      log.info("{} notification successfully sent to {}", templateType, recipientEmail);
    } catch (EmailSendException e) {
      log.error("Failed to send {} notification to {}", templateType, recipientEmail, e);
    }
  }
}
