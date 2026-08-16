package com.wcc.platform.domain.platform.feedback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.platform.type.FeedbackType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for FeedbackSearchCriteria domain object. */
class FeedbackSearchCriteriaTest {

  private FeedbackSearchCriteria criteria;

  @BeforeEach
  void setUp() {
    criteria =
        FeedbackSearchCriteria.builder()
            .reviewerId(1L)
            .revieweeId(2L)
            .mentorshipCycleId(3L)
            .feedbackType(FeedbackType.MENTOR_REVIEW)
            .year(2026)
            .isAnonymous(false)
            .isApproved(true)
            .build();
  }

  @Test
  @DisplayName("Given builder, when all fields set, then object created successfully")
  void testBuilder() {
    assertNotNull(criteria);
    assertEquals(1L, criteria.getReviewerId());
    assertEquals(2L, criteria.getRevieweeId());
    assertEquals(3L, criteria.getMentorshipCycleId());
    assertEquals(FeedbackType.MENTOR_REVIEW, criteria.getFeedbackType());
    assertEquals(2026, criteria.getYear());
    assertEquals(false, criteria.getIsAnonymous());
    assertEquals(true, criteria.getIsApproved());
  }

  @Test
  @DisplayName("Given same criteria, when equals called, then returns true")
  void testEquals() {
    FeedbackSearchCriteria sameCriteria =
        FeedbackSearchCriteria.builder()
            .reviewerId(1L)
            .revieweeId(2L)
            .mentorshipCycleId(3L)
            .feedbackType(FeedbackType.MENTOR_REVIEW)
            .year(2026)
            .isAnonymous(false)
            .isApproved(true)
            .build();

    assertEquals(criteria, sameCriteria);
  }

  @Test
  @DisplayName("Given different criteria, when equals called, then returns false")
  void testNotEquals() {
    FeedbackSearchCriteria differentCriteria =
        FeedbackSearchCriteria.builder()
            .reviewerId(99L)
            .revieweeId(2L)
            .mentorshipCycleId(3L)
            .feedbackType(FeedbackType.MENTOR_REVIEW)
            .year(2026)
            .isAnonymous(false)
            .isApproved(true)
            .build();

    assertNotEquals(criteria, differentCriteria);
  }

  @Test
  @DisplayName("Given same criteria, when hashCode called, then returns same hash")
  void testHashCode() {
    FeedbackSearchCriteria sameCriteria =
        FeedbackSearchCriteria.builder()
            .reviewerId(1L)
            .revieweeId(2L)
            .mentorshipCycleId(3L)
            .feedbackType(FeedbackType.MENTOR_REVIEW)
            .year(2026)
            .isAnonymous(false)
            .isApproved(true)
            .build();

    assertEquals(criteria.hashCode(), sameCriteria.hashCode());
  }

  @Test
  @DisplayName("Given different criteria, when hashCode called, then returns different hash")
  void testHashCodeNotEquals() {
    FeedbackSearchCriteria differentCriteria =
        FeedbackSearchCriteria.builder()
            .reviewerId(1L)
            .revieweeId(2L)
            .mentorshipCycleId(3L)
            .feedbackType(FeedbackType.COMMUNITY_GENERAL)
            .year(2026)
            .isAnonymous(false)
            .isApproved(true)
            .build();

    assertNotEquals(criteria.hashCode(), differentCriteria.hashCode());
  }

  @Test
  @DisplayName("Given criteria, when toString called, then contains field values")
  void testToString() {
    String toString = criteria.toString();

    assertTrue(toString.contains("reviewerId=1"));
    assertTrue(toString.contains("revieweeId=2"));
    assertTrue(toString.contains("mentorshipCycleId=3"));
    assertTrue(toString.contains("MENTOR_REVIEW"));
    assertTrue(toString.contains("year=2026"));
  }

  @Test
  @DisplayName("Given no-arg constructor, when created, then all fields null")
  void testNoArgsConstructor() {
    FeedbackSearchCriteria emptyCriteria = new FeedbackSearchCriteria();

    assertNull(emptyCriteria.getReviewerId());
    assertNull(emptyCriteria.getRevieweeId());
    assertNull(emptyCriteria.getMentorshipCycleId());
    assertNull(emptyCriteria.getFeedbackType());
    assertNull(emptyCriteria.getYear());
    assertNull(emptyCriteria.getIsAnonymous());
    assertNull(emptyCriteria.getIsApproved());
  }

  @Test
  @DisplayName("Given all-arg constructor, when created, then all fields set")
  void testAllArgsConstructor() {
    FeedbackSearchCriteria allArgsCriteria =
        new FeedbackSearchCriteria(
            10L, 20L, 30L, FeedbackType.MENTORSHIP_PROGRAM, 2027, true, false);

    assertEquals(10L, allArgsCriteria.getReviewerId());
    assertEquals(20L, allArgsCriteria.getRevieweeId());
    assertEquals(30L, allArgsCriteria.getMentorshipCycleId());
    assertEquals(FeedbackType.MENTORSHIP_PROGRAM, allArgsCriteria.getFeedbackType());
    assertEquals(2027, allArgsCriteria.getYear());
    assertEquals(true, allArgsCriteria.getIsAnonymous());
    assertEquals(false, allArgsCriteria.getIsApproved());
  }

  @Test
  @DisplayName("Given partial criteria, when built, then only specified fields set")
  void testPartialBuilder() {
    FeedbackSearchCriteria partialCriteria =
        FeedbackSearchCriteria.builder().reviewerId(5L).year(2025).build();

    assertEquals(5L, partialCriteria.getReviewerId());
    assertEquals(2025, partialCriteria.getYear());
    assertNull(partialCriteria.getRevieweeId());
    assertNull(partialCriteria.getMentorshipCycleId());
    assertNull(partialCriteria.getFeedbackType());
    assertNull(partialCriteria.getIsAnonymous());
    assertNull(partialCriteria.getIsApproved());
  }

  @Test
  @DisplayName("Given criteria with all types, when created, then covers all feedback types")
  void testAllFeedbackTypes() {
    FeedbackSearchCriteria mentorReviewCriteria =
        FeedbackSearchCriteria.builder().feedbackType(FeedbackType.MENTOR_REVIEW).build();
    assertEquals(FeedbackType.MENTOR_REVIEW, mentorReviewCriteria.getFeedbackType());

    FeedbackSearchCriteria mentorshipProgramCriteria =
        FeedbackSearchCriteria.builder().feedbackType(FeedbackType.MENTORSHIP_PROGRAM).build();
    assertEquals(FeedbackType.MENTORSHIP_PROGRAM, mentorshipProgramCriteria.getFeedbackType());

    FeedbackSearchCriteria communityGeneralCriteria =
        FeedbackSearchCriteria.builder().feedbackType(FeedbackType.COMMUNITY_GENERAL).build();
    assertEquals(FeedbackType.COMMUNITY_GENERAL, communityGeneralCriteria.getFeedbackType());
  }

  @Test
  @DisplayName("Given criteria with boolean values, when created, then all combinations work")
  void testBooleanFields() {
    FeedbackSearchCriteria trueTrueCriteria =
        FeedbackSearchCriteria.builder().isAnonymous(true).isApproved(true).build();
    assertEquals(true, trueTrueCriteria.getIsAnonymous());
    assertEquals(true, trueTrueCriteria.getIsApproved());

    FeedbackSearchCriteria trueFalseCriteria =
        FeedbackSearchCriteria.builder().isAnonymous(true).isApproved(false).build();
    assertEquals(true, trueFalseCriteria.getIsAnonymous());
    assertEquals(false, trueFalseCriteria.getIsApproved());

    FeedbackSearchCriteria falseTrueCriteria =
        FeedbackSearchCriteria.builder().isAnonymous(false).isApproved(true).build();
    assertEquals(false, falseTrueCriteria.getIsAnonymous());
    assertEquals(true, falseTrueCriteria.getIsApproved());

    FeedbackSearchCriteria falseFalseCriteria =
        FeedbackSearchCriteria.builder().isAnonymous(false).isApproved(false).build();
    assertEquals(false, falseFalseCriteria.getIsAnonymous());
    assertEquals(false, falseFalseCriteria.getIsApproved());
  }

  @Test
  @DisplayName("Given null values, when equals called, then handles nulls correctly")
  void testEqualsWithNulls() {
    FeedbackSearchCriteria nullCriteria1 = FeedbackSearchCriteria.builder().build();
    FeedbackSearchCriteria nullCriteria2 = FeedbackSearchCriteria.builder().build();

    assertEquals(nullCriteria1, nullCriteria2);
  }

  @Test
  @DisplayName("Given criteria with getters, when called, then returns correct values")
  void testGetters() {
    assertEquals(1L, criteria.getReviewerId());
    assertEquals(2L, criteria.getRevieweeId());
    assertEquals(3L, criteria.getMentorshipCycleId());
    assertEquals(FeedbackType.MENTOR_REVIEW, criteria.getFeedbackType());
    assertEquals(2026, criteria.getYear());
    assertEquals(false, criteria.getIsAnonymous());
    assertEquals(true, criteria.getIsApproved());
  }
}
