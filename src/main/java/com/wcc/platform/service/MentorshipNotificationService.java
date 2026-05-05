package com.wcc.platform.service;

import com.wcc.platform.configuration.NotificationConfig;
import com.wcc.platform.domain.email.EmailRequest;
import com.wcc.platform.domain.exceptions.EmailSendException;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipMatch;
import com.wcc.platform.domain.template.TemplateType;
import com.wcc.platform.repository.MemberRepository;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending mentorship-related notification emails. This service handles
 * notifications for mentor approval and rejection, leveraging the email template rendering and
 * email sending functionality.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipNotificationService {
  private final EmailTemplateService emailTemplateService;
  private final EmailService emailService;
  private final NotificationConfig notificationConfig;
  private final MemberRepository memberRepository;

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
        TemplateType.MENTOR_APPROVED,
        Map.of("mentorName", mentor.getFullName(), "mentorProfileUrl", mentorBaseUrl),
        List.of(mentor.getEmail(), notificationConfig.getMentorshipEmail()));
  }

  /**
   * Sends a mentor rejection notification email to the specified mentor.
   *
   * @param mentor the mentor to notify
   */
  public void sendMentorRejectionEmail(final Mentor mentor, final String rejectionReason) {
    sendNotification(
        TemplateType.MENTOR_PROFILE_REJECT,
        Map.of(
            "mentorshipApplicantName",
            mentor.getFullName(),
            "rejectionReason",
            rejectionReason,
            "volunteerUrl",
            notificationConfig.getVolunteerUrl()),
        List.of(mentor.getEmail()));
  }

  /** Sends a notification email to the mentor regarding mentee applications updates. */
  @SuppressWarnings("PMD.UseConcurrentHashMap") // HashMap is used to support null values
  public void sendApplicationUpdate(
      final Optional<MenteeApplication> application, final MenteeApplication updated) {

    final Map<String, Object> params =
        new HashMap<>(
            Map.of(
                "applicationId", updated.getApplicationId(),
                "menteeId", updated.getMenteeId(),
                "mentorId", Optional.ofNullable(updated.getMentorId()).orElse(0L),
                "cycleId", updated.getCycleId(),
                "priorityOrder", Optional.ofNullable(updated.getPriorityOrder()).orElse(0),
                "status", application.isPresent() ? application.get().getStatus() : "-",
                "statusUpdated", updated.getStatus(),
                "applicationMessage",
                    Optional.ofNullable(updated.getApplicationMessage()).orElse(""),
                "whyMentor", Optional.ofNullable(updated.getWhyMentor()).orElse(""),
                "mentorResponse", Optional.ofNullable(updated.getMentorResponse()).orElse("")));

    params.put("appliedAt", updated.getAppliedAt());
    params.put("reviewedAt", updated.getReviewedAt());
    params.put("matchedAt", updated.getMatchedAt());
    params.put("createdAt", updated.getCreatedAt());
    params.put("updatedAt", updated.getUpdatedAt());

    sendNotification(
        TemplateType.MENTEE_APPLICATIONS, params, List.of(notificationConfig.getMentorshipEmail()));
  }

  /**
   * Renders an email template and sends a notification email to the specified recipient.
   *
   * @param recipientEmails the list of recipient's email address
   * @param templateType the type of template to render
   * @param templateParams the parameters to use for rendering the template
   */
  /* default */ void sendNotification(
      final TemplateType templateType,
      final Map<String, Object> templateParams,
      final List<String> recipientEmails) {
    try {
      final var template = emailTemplateService.renderTemplate(templateType, templateParams);

      final var emailRequest =
          EmailRequest.builder()
              .recipients(recipientEmails)
              .subject(template.subject())
              .body(template.body())
              .html(true)
              .build();

      emailService.sendEmail(emailRequest);
      log.info("{} notification successfully sent to {}", templateType, recipientEmails);
    } catch (EmailSendException e) {
      log.error("Failed to send {} notification to {}", templateType, recipientEmails, e);
    }
  }

  /**
   * Renders an email template and sends a notification email to the specified recipient and always
   * send copy to the mentorship team.
   *
   * @param memberIds the list of members ids to send notifications
   * @param templateType the type of template to render
   * @param templateParams the parameters to use for rendering the template
   */
  public void sendNotificationByMemberId(
      final TemplateType templateType,
      final Map<String, Object> templateParams,
      final List<Long> memberIds) {

    final var recipientEmails =
        new ArrayList<>(
            memberRepository.findEmails(memberIds).stream().filter(Objects::nonNull).toList());

    final String teamEmail = notificationConfig.getMentorshipEmail();
    if (StringUtils.isNotBlank(teamEmail)) {
      recipientEmails.add(teamEmail);
    }

    sendNotification(templateType, templateParams, recipientEmails);
  }

  /** Sends a notification email to the mentor regarding mentee applications updates. */
  @SuppressWarnings("PMD.UseConcurrentHashMap") // HashMap is used to support null values
  public void sendMatchUpdate(
      final Optional<MentorshipMatch> previous, final MentorshipMatch updated) {

    final Map<String, Object> params =
        new HashMap<>(
            Map.of(
                "matchId", Optional.ofNullable(updated.getMatchId()).orElse(0L),
                "mentorId", updated.getMentorId(),
                "menteeId", updated.getMenteeId(),
                "cycleId", updated.getCycleId(),
                "applicationId", Optional.ofNullable(updated.getApplicationId()).orElse(0L),
                "status", previous.isPresent() ? previous.get().getStatus() : "-",
                "statusUpdated", updated.getStatus(),
                "sessionFrequency", Optional.ofNullable(updated.getSessionFrequency()).orElse(""),
                "totalSessions", Optional.ofNullable(updated.getTotalSessions()).orElse(0),
                "cancellationReason",
                    Optional.ofNullable(updated.getCancellationReason()).orElse("")));

    params.put("cancelledBy", updated.getCancelledBy());
    params.put("startDate", updated.getStartDate());
    params.put("endDate", updated.getEndDate());
    params.put("expectedEndDate", updated.getExpectedEndDate());
    params.put("cancelledAt", updated.getCancelledAt());
    params.put("createdAt", updated.getCreatedAt());
    params.put("updatedAt", updated.getUpdatedAt());

    sendNotification(
        TemplateType.MATCH_APPLICATIONS, params, List.of(notificationConfig.getMentorshipEmail()));
  }
}
