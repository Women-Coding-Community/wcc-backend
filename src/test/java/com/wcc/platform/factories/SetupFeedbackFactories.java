package com.wcc.platform.factories;

import com.wcc.platform.domain.platform.feedback.Feedback;
import com.wcc.platform.domain.platform.feedback.FeedbackDto;
import com.wcc.platform.domain.platform.type.FeedbackType;
import java.time.OffsetDateTime;

/** Setup Factory for Feedback tests. */
public class SetupFeedbackFactories {

  private static final Long REVIEWER_ID = 1L;
  private static final Long REVIEWEE_ID = 2L;
  private static final String REVIEWER_NAME = "Mentee Reviewer";
  private static final String REVIEWEE_NAME = "Mentor Reviewee";
  private static final String FEEDBACK_TEXT = "This is a test feedback";
  private static final Integer RATING = 5;
  private static final Integer YEAR = 2026;

  /**
   * Create a test FeedbackDto for MENTOR_REVIEW type.
   *
   * @return FeedbackDto
   */
  public static FeedbackDto createMentorReviewFeedbackDtoTest() {
    return FeedbackDto.builder()
        .reviewerId(REVIEWER_ID)
        .revieweeId(REVIEWEE_ID)
        .mentorshipCycleId(1L)
        .feedbackType(FeedbackType.MENTOR_REVIEW)
        .rating(RATING)
        .feedbackText(FEEDBACK_TEXT)
        .year(YEAR)
        .isAnonymous(false)
        .build();
  }

  /**
   * Create a test FeedbackDto for COMMUNITY_GENERAL type.
   *
   * @return FeedbackDto
   */
  public static FeedbackDto createCommunityGeneralFeedbackDtoTest() {
    return FeedbackDto.builder()
        .reviewerId(REVIEWER_ID)
        .feedbackType(FeedbackType.COMMUNITY_GENERAL)
        .rating(4)
        .feedbackText("Great community experience")
        .year(YEAR)
        .isAnonymous(true)
        .build();
  }

  /**
   * Create a test FeedbackDto for MENTORSHIP_PROGRAM type.
   *
   * @return FeedbackDto
   */
  public static FeedbackDto createMentorshipProgramFeedbackDtoTest() {
    return FeedbackDto.builder()
        .reviewerId(REVIEWER_ID)
        .mentorshipCycleId(1L)
        .feedbackType(FeedbackType.MENTORSHIP_PROGRAM)
        .rating(5)
        .feedbackText("Excellent mentorship program")
        .year(YEAR)
        .isAnonymous(false)
        .build();
  }

  /**
   * Create a test Feedback domain object for MENTOR_REVIEW type.
   *
   * @return Feedback
   */
  public static Feedback createMentorReviewFeedbackTest() {
    return Feedback.builder()
        .id(1L)
        .reviewerId(REVIEWER_ID)
        .reviewerName(REVIEWER_NAME)
        .revieweeId(REVIEWEE_ID)
        .revieweeName(REVIEWEE_NAME)
        .mentorshipCycleId(1L)
        .feedbackType(FeedbackType.MENTOR_REVIEW)
        .rating(RATING)
        .feedbackText(FEEDBACK_TEXT)
        .year(YEAR)
        .isAnonymous(true)
        .isApproved(false)
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .build();
  }

  /**
   * Create a test Feedback domain object for COMMUNITY_GENERAL type.
   *
   * @return Feedback
   */
  public static Feedback createCommunityGeneralFeedbackTest() {
    return Feedback.builder()
        .id(2L)
        .reviewerId(REVIEWER_ID)
        .reviewerName(REVIEWER_NAME)
        .feedbackType(FeedbackType.COMMUNITY_GENERAL)
        .rating(4)
        .feedbackText("Great community experience")
        .year(YEAR)
        .isAnonymous(false)
        .isApproved(true)
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .build();
  }

  /**
   * Create a test Feedback domain object for MENTORSHIP_PROGRAM type.
   *
   * @return Feedback
   */
  public static Feedback createMentorshipProgramFeedbackTest() {
    return Feedback.builder()
        .id(3L)
        .reviewerId(REVIEWER_ID)
        .reviewerName(REVIEWER_NAME)
        .mentorshipCycleId(1L)
        .feedbackType(FeedbackType.MENTORSHIP_PROGRAM)
        .rating(5)
        .feedbackText("Excellent mentorship program")
        .year(YEAR)
        .isAnonymous(false)
        .isApproved(true)
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .build();
  }

  /**
   * Create a test approved and non-anonymous Feedback (reviewer name visible).
   *
   * @return Feedback
   */
  public static Feedback createApprovedPublicFeedbackTest() {
    final Feedback feedback = createMentorReviewFeedbackTest();
    feedback.setIsApproved(true);
    feedback.setIsAnonymous(false);
    return feedback;
  }

  /**
   * Create a test pending approval Feedback.
   *
   * @return Feedback
   */
  public static Feedback createPendingApprovalFeedbackTest() {
    final Feedback feedback = createMentorReviewFeedbackTest();
    feedback.setIsApproved(false);
    feedback.setIsAnonymous(true);
    return feedback;
  }
}
