package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.ApplicationMenteeWorkflowException;
import com.wcc.platform.domain.exceptions.ApplicationNotFoundException;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.MentorCapacityExceededException;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.MenteeApplicationReviewDto;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing mentee applications to mentors. Handles application submission, status
 * updates, and workflow transitions.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenteeWorkflowService {

  private final MenteeApplicationRepository applicationRepository;
  private final MenteeRepository menteeRepository;
  private final MentorshipMatchRepository matchRepository;
  private final MentorshipCycleRepository cycleRepository;

  /**
   * Admin approves a mentee application.
   *
   * @param applicationId the application ID
   * @return updated application
   * @throws ApplicationNotFoundException if application not found
   */
  @Transactional
  public MenteeApplication approveApplication(final Long applicationId) {
    final MenteeApplication application = getApplicationOrThrow(applicationId);

    if (application.getStatus() != ApplicationStatus.PENDING) {
      throw new ContentNotFoundException("No pending application with id " + applicationId);
    }

    final MenteeApplication updated =
        applicationRepository.updateStatus(applicationId, ApplicationStatus.MENTOR_REVIEWING, null);

    log.info(
        "Application {} from mentee {} approved and to be reviewed by mentor {}",
        applicationId,
        application.getMenteeId(),
        application.getMentorId());

    return updated;
  }

  /**
   * Admin rejects a mentee application.
   *
   * @param applicationId the application ID
   * @param reason reason for rejection
   * @return updated application
   * @throws ApplicationNotFoundException if application not found
   */
  @Transactional
  public MenteeApplication rejectApplication(final Long applicationId, final String reason) {
    final MenteeApplication application = getApplicationOrThrow(applicationId);

    if (application.getStatus() != ApplicationStatus.PENDING) {
      throw new ContentNotFoundException("No pending application with id " + applicationId);
    }

    final MenteeApplication updated =
        applicationRepository.updateStatus(applicationId, ApplicationStatus.REJECTED, reason);

    log.info(
        "Application {} from mentee {} rejected by the Mentorship Team",
        applicationId,
        application.getMenteeId());

    return updated;
  }

  /**
   * Mentor accepts an application.
   *
   * @param applicationId the application ID
   * @param mentorResponse optional response message from mentor
   * @return updated application
   * @throws ApplicationNotFoundException if application not found
   * @throws MentorCapacityExceededException if mentor at capacity
   */
  @Transactional
  public MenteeApplication acceptApplication(
      final Long applicationId, final String mentorResponse) {

    final MenteeApplication application = getApplicationOrThrow(applicationId);

    validateApplicationCanBeAccepted(application);
    checkMentorCapacity(application.getMentorId(), application.getCycleId());

    final MenteeApplication updated =
        applicationRepository.updateStatus(
            applicationId, ApplicationStatus.MENTOR_ACCEPTED, mentorResponse);

    log.info(
        "Mentor {} accepted application {} from mentee {}",
        application.getMentorId(),
        applicationId,
        application.getMenteeId());

    return updated;
  }

  /**
   * Mentor declines an application. Automatically notifies next priority mentor if available.
   *
   * @param applicationId the application ID
   * @param reason reason for declining
   * @return updated application
   * @throws ApplicationNotFoundException if application not found
   */
  @Transactional
  public MenteeApplication declineApplication(final Long applicationId, final String reason) {

    final MenteeApplication application = getApplicationOrThrow(applicationId);

    final MenteeApplication updated =
        applicationRepository.updateStatus(
            applicationId, ApplicationStatus.MENTOR_DECLINED, reason);

    log.info(
        "Mentor {} declined application {} from mentee {}",
        application.getMentorId(),
        applicationId,
        application.getMenteeId());

    // Auto-notify next priority mentor
    notifyNextPriorityMentor(application);

    return updated;
  }

  /**
   * Mentee withdraws (drops) an application.
   *
   * @param applicationId the application ID
   * @param reason reason for withdrawing
   * @return updated application
   * @throws ApplicationNotFoundException if application not found
   */
  @Transactional
  public MenteeApplication withdrawApplication(final Long applicationId, final String reason) {

    final MenteeApplication application = getApplicationOrThrow(applicationId);

    final MenteeApplication updated =
        applicationRepository.updateStatus(applicationId, ApplicationStatus.DROPPED, reason);

    log.info("Mentee {} withdrew application {}", application.getMenteeId(), applicationId);

    return updated;
  }

  /**
   * Get all applications for a mentee in a specific cycle, ordered by priority.
   *
   * @param menteeId the mentee ID
   * @param cycleId the cycle ID
   * @return list of applications ordered by priority
   */
  public List<MenteeApplication> getMenteeApplications(final Long menteeId, final Long cycleId) {
    if (menteeId == null) {
      return List.of();
    }

    return applicationRepository.findByMenteeAndCycleOrderByPriority(menteeId, cycleId);
  }

  /**
   * Get all applications to a specific mentor.
   *
   * @param mentorId the mentor ID
   * @return list of applications
   */
  public List<MenteeApplication> getMentorApplications(final Long mentorId) {
    return applicationRepository.findByMentor(mentorId);
  }

  /**
   * Get applications by status.
   *
   * @param status the application status
   * @return list of applications with that status
   */
  public List<MenteeApplication> getApplicationsByStatus(final ApplicationStatus status) {
    return applicationRepository.findByStatus(status);
  }

  /**
   * Returns all PENDING priority-1 mentee applications enriched with mentee profile data, for admin
   * review.
   *
   * @return list of review DTOs containing application and mentee details
   */
  public List<MenteeApplicationReviewDto> getPendingPriorityOneReviews() {
    return applicationRepository
        .findByStatusAndPriorityOrder(ApplicationStatus.PENDING, 1)
        .stream()
        .map(this::toReviewDto)
        .toList();
  }

  /**
   * Admin approves a mentee by mentee ID. Only the priority-1 PENDING application is moved to
   * MENTOR_REVIEWING; all other PENDING applications remain unchanged.
   *
   * @param menteeId the mentee ID
   * @return the approved application
   * @throws ContentNotFoundException if no priority-1 PENDING application exists for the mentee
   */
  @Transactional
  public MenteeApplication approveMenteeByMenteeId(final Long menteeId) {
    final List<MenteeApplication> pending = applicationRepository.findPendingByMenteeId(menteeId);

    final MenteeApplication priorityOne =
        pending.stream()
            .filter(app -> app.getPriorityOrder() == 1)
            .findFirst()
            .orElseThrow(
                () ->
                    new ContentNotFoundException(
                        "No pending priority-1 application found for mentee " + menteeId));

    final MenteeApplication updated =
        applicationRepository.updateStatus(
            priorityOne.getApplicationId(), ApplicationStatus.MENTOR_REVIEWING, null);

    log.info(
        "Mentee {} priority-1 application {} approved and forwarded to mentor {}",
        menteeId,
        priorityOne.getApplicationId(),
        priorityOne.getMentorId());

    return updated;
  }

  /**
   * Admin rejects all PENDING applications for a mentee by mentee ID.
   *
   * @param menteeId the mentee ID
   * @param reason the reason for rejection
   * @return list of all rejected applications
   * @throws ContentNotFoundException if no PENDING applications exist for the mentee
   */
  @Transactional
  public List<MenteeApplication> rejectMenteeByMenteeId(
      final Long menteeId, final String reason) {
    final List<MenteeApplication> pending = applicationRepository.findPendingByMenteeId(menteeId);

    if (pending.isEmpty()) {
      throw new ContentNotFoundException(
          "No pending applications found for mentee " + menteeId);
    }

    final List<MenteeApplication> rejected =
        pending.stream()
            .map(
                app ->
                    applicationRepository.updateStatus(
                        app.getApplicationId(), ApplicationStatus.REJECTED, reason))
            .toList();

    log.info(
        "All {} pending applications for mentee {} rejected by the Mentorship Team",
        rejected.size(),
        menteeId);

    return rejected;
  }

  private MenteeApplicationReviewDto toReviewDto(final MenteeApplication application) {
    return menteeRepository
        .findById(application.getMenteeId())
        .map(
            mentee -> {
              final String linkedinUrl =
                  mentee.getNetwork() == null
                      ? null
                      : mentee.getNetwork().stream()
                          .filter(n -> n.type() == SocialNetworkType.LINKEDIN)
                          .findFirst()
                          .map(SocialNetwork::link)
                          .orElse(null);

              final Integer yearsExperience =
                  mentee.getSkills() != null ? mentee.getSkills().yearsExperience() : null;

              return new MenteeApplicationReviewDto(
                  application.getApplicationId(),
                  mentee.getId(),
                  mentee.getFullName(),
                  mentee.getPosition(),
                  yearsExperience,
                  linkedinUrl,
                  mentee.getSlackDisplayName(),
                  mentee.getEmail(),
                  application.getWhyMentor());
            })
        .orElseThrow(
            () ->
                new ContentNotFoundException(
                    "Mentee not found for application " + application.getApplicationId()));
  }

  private MenteeApplication getApplicationOrThrow(final Long applicationId) {
    return applicationRepository
        .findById(applicationId)
        .orElseThrow(() -> new ApplicationNotFoundException(applicationId));
  }

  private void validateApplicationCanBeAccepted(final MenteeApplication application) {
    if (!application.canBeModified()) {
      throw new ApplicationMenteeWorkflowException(
          "Application is in terminal state: " + application.getStatus());
    }
  }

  private void checkMentorCapacity(final Long mentorId, final Long cycleId) {
    final MentorshipCycleEntity cycle =
        cycleRepository
            .findById(cycleId)
            .orElseThrow(() -> new IllegalArgumentException("Cycle not found: " + cycleId));

    final int currentMentees =
        matchRepository.countActiveMenteesByMentorAndCycle(mentorId, cycleId);

    if (currentMentees >= cycle.getMaxMenteesPerMentor()) {
      throw new MentorCapacityExceededException(
          String.format(
              "Mentor %d has reached maximum capacity (%d) for cycle %d",
              mentorId, cycle.getMaxMenteesPerMentor(), cycleId));
    }
  }

  private void notifyNextPriorityMentor(final MenteeApplication declinedApplication) {
    final List<MenteeApplication> allApplications =
        applicationRepository.findByMenteeAndCycleOrderByPriority(
            declinedApplication.getMenteeId(), declinedApplication.getCycleId());

    allApplications.stream()
        .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
        .filter(app -> app.getPriorityOrder() > declinedApplication.getPriorityOrder())
        .findFirst()
        .ifPresent(
            nextApp -> {
              log.info(
                  "Next priority mentor {} will be notified for mentee {}",
                  nextApp.getMentorId(),
                  nextApp.getMenteeId());
              // TODO: Send email notification to next priority mentor
            });
  }
}
