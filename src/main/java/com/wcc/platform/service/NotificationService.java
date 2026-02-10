package com.wcc.platform.service;

import com.wcc.platform.domain.email.EmailRequest;
import com.wcc.platform.domain.template.RenderedTemplate;
import com.wcc.platform.domain.template.TemplateType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
  private final EmailTemplateService emailTemplateService;
  private final EmailService emailService;

  public void sendNotification(
      final String recipientEmail,
      final TemplateType templateType,
      final Map<String, String> templateParams) {
    try {
      final RenderedTemplate template =
          emailTemplateService.renderTemplate(templateType, templateParams);

      final EmailRequest emailRequest =
          EmailRequest.builder()
              .to(recipientEmail)
              .subject(template.subject())
              .body(template.body())
              .html(true)
              .build();

      emailService.sendEmail(emailRequest);
      log.info("{} notification successfully sent to {}", templateType, recipientEmail);

    } catch (Exception e) {
      log.error("Failed to send {} notification to {}", templateType, recipientEmail, e);
    }
  }
}
