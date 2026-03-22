package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.ApplicationMenteeWorkflowException;
import com.wcc.platform.domain.exceptions.ApplicationNotFoundException;
import com.wcc.platform.domain.exceptions.MentorCapacityExceededException;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.repository.MenteeApplicationRepository;
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
  private final MentorshipMatchRepository matchRepository;
  private final MentorshipCycleRepository cycleRepository;

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
