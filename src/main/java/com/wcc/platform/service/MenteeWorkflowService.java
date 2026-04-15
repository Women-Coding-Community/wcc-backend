package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.ApplicationMenteeWorkflowException;
import com.wcc.platform.domain.exceptions.ApplicationNotFoundException;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.MentorCapacityExceededException;
import com.wcc.platform.domain.exceptions.MentorNotFoundException;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MentorRepository;
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
  private final MentorRepository mentorRepository;
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
   * Admin rejects a mentee application. Automatically forwards to next priority mentor if
   * available. If no next mentor is available, triggers manual match.
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

    final boolean forwarded = forwardToNextPriorityMentor(application);
    if (!forwarded) {
      checkAndTriggerManualMatch(application.getMenteeId(), application.getCycleId());
    }

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
   * Mentor declines an application. Automatically forwards to next priority mentor if available. If
   * no next mentor is available, triggers manual match.
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

    final boolean forwarded = forwardToNextPriorityMentor(application);
    if (!forwarded) {
      checkAndTriggerManualMatch(application.getMenteeId(), application.getCycleId());
    }

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
   * Manually assigns a mentor to a mentee from the manual match queue. Validates mentor exists and
   * has capacity, closes the PENDING_MANUAL_MATCH placeholder, and creates a new application.
   *
   * @param menteeId the mentee ID
   * @param cycleId the cycle ID
   * @param mentorId the mentor ID to assign
   * @param notes optional notes for the assignment
   * @return the newly created application
   * @throws ContentNotFoundException if mentor not found or no pending manual match exists
   * @throws MentorCapacityExceededException if mentor is at capacity
   */
  @Transactional
  public MenteeApplication assignMentor(
      final Long menteeId, final Long cycleId, final Long mentorId, final String notes) {
    if (mentorRepository.findById(mentorId).isEmpty()) {
      throw new MentorNotFoundException(mentorId);
    }

    checkMentorCapacity(mentorId, cycleId);

    final var manualMatchApps =
        applicationRepository.findByMenteeCycleAndStatus(
            menteeId, cycleId, ApplicationStatus.PENDING_MANUAL_MATCH);

    if (manualMatchApps.isEmpty()) {
      throw new ContentNotFoundException(
          String.format(
              "No PENDING_MANUAL_MATCH application found for mentee %d in cycle %d",
              menteeId, cycleId));
    }

    final MenteeApplication manualMatchApp = manualMatchApps.get();
    applicationRepository.updateStatus(
        manualMatchApp.getApplicationId(), ApplicationStatus.REJECTED, "Manually assigned mentor");

    final MenteeApplication newApplication =
        MenteeApplication.builder()
            .menteeId(menteeId)
            .mentorId(mentorId)
            .cycleId(cycleId)
            .priorityOrder(null)
            .status(ApplicationStatus.PENDING)
            .applicationMessage(notes)
            .build();

    final MenteeApplication created = applicationRepository.create(newApplication);

    log.info("Manually assigned mentor {} to mentee {} in cycle {}", mentorId, menteeId, cycleId);

    return created;
  }

  /**
   * Confirms that no match was found for a mentee in the manual match queue. Sets the
   * PENDING_MANUAL_MATCH application to NO_MATCH_FOUND status. This is a terminal state - no
   * further actions possible for this mentee in this cycle.
   *
   * @param menteeId the mentee ID
   * @param cycleId the cycle ID
   * @param reason the reason no match was found
   * @return the updated application
   * @throws ContentNotFoundException if no pending manual match exists
   */
  @Transactional
  public MenteeApplication confirmNoMatch(
      final Long menteeId, final Long cycleId, final String reason) {

    final var manualMatchApps =
        applicationRepository.findByMenteeCycleAndStatus(
            menteeId, cycleId, ApplicationStatus.PENDING_MANUAL_MATCH);

    if (manualMatchApps.isEmpty()) {
      throw new ContentNotFoundException(
          String.format(
              "No PENDING_MANUAL_MATCH application found for mentee %d in cycle %d",
              menteeId, cycleId));
    }

    final MenteeApplication manualMatchApp = manualMatchApps.get();
    final MenteeApplication updated =
        applicationRepository.updateStatus(
            manualMatchApp.getApplicationId(), ApplicationStatus.NO_MATCH_FOUND, reason);

    log.info(
        "Confirmed no match found for mentee {} in cycle {}. Reason: {}",
        menteeId,
        cycleId,
        reason);

    return updated;
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

  /**
   * Forwards to the next priority mentor by finding the next pending application with a higher
   * priority order and moves it to MENTOR_REVIEWING status.
   *
   * @param currentApplication the application that was just rejected/declined
   * @return true if forwarding succeeded, false if no next mentor available
   */
  private boolean forwardToNextPriorityMentor(final MenteeApplication currentApplication) {
    final List<MenteeApplication> allApplications =
        applicationRepository.findByMenteeAndCycleOrderByPriority(
            currentApplication.getMenteeId(), currentApplication.getCycleId());

    final var nextApplication =
        allApplications.stream()
            .filter(app -> app.getStatus() == ApplicationStatus.PENDING)
            .filter(app -> app.getPriorityOrder() > currentApplication.getPriorityOrder())
            .findFirst();

    if (nextApplication.isPresent()) {
      final MenteeApplication nextApp = nextApplication.get();
      applicationRepository.updateStatus(
          nextApp.getApplicationId(), ApplicationStatus.MENTOR_REVIEWING, null);

      log.info(
          "Forwarded application to next priority mentor {} for mentee {}",
          nextApp.getMentorId(),
          nextApp.getMenteeId());
      // TODO: Send email notification to next priority mentor
      return true;
    }

    return false;
  }

  /**
   * Checks if all applications for a mentee in a cycle are in non-forwardable states (cannot lead
   * to a match). If so, creates a PENDING_MANUAL_MATCH application to indicate the mentee needs
   * manual matching by the mentorship team.
   *
   * @param menteeId the mentee ID
   * @param cycleId the cycle ID
   */
  private void checkAndTriggerManualMatch(final Long menteeId, final Long cycleId) {
    final List<MenteeApplication> allApplications =
        applicationRepository.findByMenteeAndCycleOrderByPriority(menteeId, cycleId);

    final boolean allNonForwardable =
        !allApplications.isEmpty()
            && allApplications.stream()
                .filter(app -> app.getMentorId() != null)
                .allMatch(app -> app.getStatus().isNonForwardable());

    if (allNonForwardable) {
      final var existingManualMatch =
          applicationRepository.findByMenteeCycleAndStatus(
              menteeId, cycleId, ApplicationStatus.PENDING_MANUAL_MATCH);

      if (existingManualMatch.isEmpty()) {
        final MenteeApplication manualMatchApp =
            MenteeApplication.builder()
                .menteeId(menteeId)
                .mentorId(null)
                .cycleId(cycleId)
                .priorityOrder(null)
                .status(ApplicationStatus.PENDING_MANUAL_MATCH)
                .applicationMessage("All mentor applications exhausted - requires manual matching")
                .build();

        applicationRepository.create(manualMatchApp);

        log.info(
            "Created PENDING_MANUAL_MATCH application for mentee {} in cycle {} "
                + "as all applications are non-forwardable",
            menteeId,
            cycleId);
      }
    }
  }
}
