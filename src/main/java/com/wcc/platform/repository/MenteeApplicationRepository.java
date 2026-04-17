package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing mentee applications to mentors. Supports priority-based mentor
 * selection and application workflow tracking.
 */
public interface MenteeApplicationRepository extends CrudRepository<MenteeApplication, Long> {

  /**
   * Find all applications for a specific mentee in a cycle.
   *
   * @param menteeId the mentee ID
   * @param cycleId the cycle ID
   * @return list of applications
   */
  List<MenteeApplication> findByMenteeAndCycle(Long menteeId, Long cycleId);

  /**
   * Find all applications to a specific mentor.
   *
   * @param mentorId the mentor ID
   * @return list of applications to this mentor
   */
  List<MenteeApplication> findByMentor(Long mentorId);

  /**
   * Find all applications with a specific status.
   *
   * @param status the application status
   * @return list of applications with this status
   */
  List<MenteeApplication> findByStatus(ApplicationStatus status);

  /**
   * Find a specific application by mentee, mentor, and cycle.
   *
   * @param menteeId the mentee ID
   * @param mentorId the mentor ID
   * @param cycleId the cycle ID
   * @return Optional containing the application if found
   */
  Optional<MenteeApplication> findByMenteeMentorCycle(Long menteeId, Long mentorId, Long cycleId);

  /**
   * Find applications for a mentee in a cycle, ordered by priority.
   *
   * @param menteeId the mentee ID
   * @param cycleId the cycle ID
   * @return list of applications ordered by priority (1 = highest)
   */
  List<MenteeApplication> findByMenteeAndCycleOrderByPriority(Long menteeId, Long cycleId);

  /**
   * Update the status of an application.
   *
   * @param applicationId the application ID
   * @param newStatus the new status
   * @param notes optional notes explaining the status change
   * @return the updated application
   */
  MenteeApplication updateStatus(Long applicationId, ApplicationStatus newStatus, String notes);

  /**
   * Get all mentee applications.
   *
   * @return list of all applications
   */
  List<MenteeApplication> getAll();
  
  /**
   * Counts the number of mentee applications for a specific mentee in a specific cycle.
   *
   * @param menteeId the unique identifier of the mentee whose applications are to be counted
   * @param cycleId the unique identifier of the cycle within which the applications are to be
   *     counted
   * @return the total number of applications submitted by the mentee in the specified cycle
   */
  Long countMenteeApplications(Long menteeId, Long cycleId);

  /**
   * Find all PENDING applications for a specific mentee across all cycles, ordered by priority.
   *
   * @param menteeId the mentee ID
   * @return list of PENDING applications for the mentee ordered by priority
   */
  List<MenteeApplication> findPendingByMenteeId(Long menteeId);

  /**
   * Find applications for a mentee in a cycle with a specific status, ordered by priority.
   *
   * @param menteeId the mentee ID
   * @param cycleId the cycle ID
   * @param status the application status
   * @return list of applications matching the criteria ordered by priority (1 = highest)
   */
  List<MenteeApplication> findByMenteeCycleAndStatusOrderByPriority(
      Long menteeId, Long cycleId, ApplicationStatus status);

  /**
   * Find all applications for a status and cycle
   *
   * @param status The application status
   * @param cycleId The current mentorship cycle
   * @return List of applications
   */
  List<MenteeApplication> findByStatusAndCycle(ApplicationStatus status, Long cycleId);
}
