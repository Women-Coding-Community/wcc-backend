package com.wcc.platform.utils;

import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;

/** Test utility class for building MenteeApplication instances with various statuses. */
public final class MenteeApplicationTestBuilder {

  private static final Long DEFAULT_MENTOR_ID = 20L;
  private static final Long DEFAULT_CYCLE_ID = 5L;
  private static final String DEFAULT_WHY_MENTOR = "Great mentor";

  private MenteeApplicationTestBuilder() {}

  /**
   * Creates a PENDING application.
   *
   * @param applicationId the application ID
   * @param menteeId the mentee ID
   * @param priority the priority order
   * @return a pending MenteeApplication
   */
  public static MenteeApplication pending(
      final Long applicationId, final Long menteeId, final int priority) {
    return baseBuilder(applicationId, menteeId, priority)
        .status(ApplicationStatus.PENDING)
        .build();
  }

  /**
   * Creates a MENTOR_REVIEWING application.
   *
   * @param applicationId the application ID
   * @param menteeId the mentee ID
   * @param priority the priority order
   * @return a reviewing MenteeApplication
   */
  public static MenteeApplication reviewing(
      final Long applicationId, final Long menteeId, final int priority) {
    return baseBuilder(applicationId, menteeId, priority)
        .status(ApplicationStatus.MENTOR_REVIEWING)
        .build();
  }

  /**
   * Creates a REJECTED application.
   *
   * @param applicationId the application ID
   * @param menteeId the mentee ID
   * @param priority the priority order
   * @return a rejected MenteeApplication
   */
  public static MenteeApplication rejected(
      final Long applicationId, final Long menteeId, final int priority) {
    return baseBuilder(applicationId, menteeId, priority)
        .status(ApplicationStatus.REJECTED)
        .build();
  }

  /**
   * Creates a MENTOR_DECLINED application.
   *
   * @param applicationId the application ID
   * @param menteeId the mentee ID
   * @param priority the priority order
   * @return a declined MenteeApplication
   */
  public static MenteeApplication declined(
      final Long applicationId, final Long menteeId, final int priority) {
    return baseBuilder(applicationId, menteeId, priority)
        .status(ApplicationStatus.MENTOR_DECLINED)
        .build();
  }

  /**
   * Creates a MATCHED application.
   *
   * @param applicationId the application ID
   * @param menteeId the mentee ID
   * @param priority the priority order
   * @return a matched MenteeApplication
   */
  public static MenteeApplication matched(
      final Long applicationId, final Long menteeId, final int priority) {
    return baseBuilder(applicationId, menteeId, priority)
        .status(ApplicationStatus.MATCHED)
        .build();
  }

  /**
   * Creates a PENDING_MANUAL_MATCH application (mentor_id and priority_order are null).
   *
   * @param applicationId the application ID
   * @param menteeId the mentee ID
   * @return a pending manual match MenteeApplication
   */
  public static MenteeApplication pendingManualMatch(final Long applicationId, final Long menteeId) {
    return MenteeApplication.builder()
        .applicationId(applicationId)
        .menteeId(menteeId)
        .mentorId(null)
        .cycleId(DEFAULT_CYCLE_ID)
        .priorityOrder(null)
        .status(ApplicationStatus.PENDING_MANUAL_MATCH)
        .build();
  }

  /**
   * Creates a base builder with common fields.
   *
   * @param applicationId the application ID
   * @param menteeId the mentee ID
   * @param priority the priority order
   * @return a MenteeApplication builder with common fields set
   */
  public static MenteeApplication.MenteeApplicationBuilder baseBuilder(
      final Long applicationId, final Long menteeId, final int priority) {
    return MenteeApplication.builder()
        .applicationId(applicationId)
        .menteeId(menteeId)
        .mentorId(DEFAULT_MENTOR_ID)
        .cycleId(DEFAULT_CYCLE_ID)
        .priorityOrder(priority)
        .whyMentor(DEFAULT_WHY_MENTOR);
  }

  /**
   * Creates a base builder with custom mentor ID.
   *
   * @param applicationId the application ID
   * @param menteeId the mentee ID
   * @param mentorId the mentor ID
   * @param priority the priority order
   * @return a MenteeApplication builder with common fields set
   */
  public static MenteeApplication.MenteeApplicationBuilder baseBuilder(
      final Long applicationId, final Long menteeId, final Long mentorId, final int priority) {
    return MenteeApplication.builder()
        .applicationId(applicationId)
        .menteeId(menteeId)
        .mentorId(mentorId)
        .cycleId(DEFAULT_CYCLE_ID)
        .priorityOrder(priority)
        .whyMentor(DEFAULT_WHY_MENTOR);
  }
}