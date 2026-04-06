package com.wcc.platform.domain.platform.feedback;

import static com.wcc.platform.factories.SetupFeedbackFactories.createCommunityGeneralFeedbackTest;
import static com.wcc.platform.factories.SetupFeedbackFactories.createMentorReviewFeedbackTest;
import static com.wcc.platform.factories.SetupFeedbackFactories.createMentorshipProgramFeedbackTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.platform.type.FeedbackType;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test for class {@link Feedback}. */
class FeedbackTest {

  private Feedback feedback;

  @BeforeEach
  void setUp() {
    feedback = createMentorReviewFeedbackTest();
  }

  @Test
  @DisplayName("Given same feedback, when equals called, then returns true")
  void testEquals() {
    Feedback sameFeedback = feedback.toBuilder().build();
    assertEquals(feedback, sameFeedback);
  }

  @Test
  @DisplayName("Given different feedback types, when equals called, then returns false")
  void testNotEquals() {
    Feedback communityFeedback = createCommunityGeneralFeedbackTest();
    Feedback programFeedback = createMentorshipProgramFeedbackTest();
    Feedback emptyFeedback = new Feedback();

    assertNotEquals(feedback, communityFeedback);
    assertNotEquals(feedback, programFeedback);
    assertNotEquals(feedback, emptyFeedback);
  }

  @Test
  @DisplayName("Given feedback with null field, when equals called, then handles null correctly")
  void testEqualsWithNullFields() {
    Feedback feedback1 = Feedback.builder().id(1L).reviewerId(1L).build();
    Feedback feedback2 = Feedback.builder().id(1L).reviewerId(1L).build();
    Feedback feedback3 = Feedback.builder().id(2L).reviewerId(1L).build();

    assertEquals(feedback1, feedback2);
    assertNotEquals(feedback1, feedback3);
  }

  @Test
  @DisplayName("Given same feedback, when hashCode called, then returns same hash")
  void testHashCode() {
    Feedback sameFeedback = feedback.toBuilder().build();
    assertEquals(feedback.hashCode(), sameFeedback.hashCode());
  }

  @Test
  @DisplayName("Given different feedback, when hashCode called, then returns different hash")
  void testHashCodeNotEquals() {
    Feedback differentFeedback = createCommunityGeneralFeedbackTest();
    assertNotEquals(feedback.hashCode(), differentFeedback.hashCode());
  }

  @Test
  @DisplayName("Given feedback with null fields, when hashCode called, then handles null")
  void testHashCodeWithNullFields() {
    Feedback feedback1 = Feedback.builder().id(1L).build();
    Feedback feedback2 = Feedback.builder().id(1L).build();
    assertEquals(feedback1.hashCode(), feedback2.hashCode());
  }

  @Test
  @DisplayName("Given feedback, when toString called, then contains feedback type")
  void testToString() {
    String result = feedback.toString();
    assertTrue(result.contains(FeedbackType.MENTOR_REVIEW.toString()));
    assertTrue(result.contains("reviewerId"));
  }

  @Test
  @DisplayName("Given feedback, when toString called with null fields, then handles null")
  void testToStringWithNullFields() {
    Feedback emptyFeedback = new Feedback();
    String result = emptyFeedback.toString();
    assertNotNull(result);
    assertTrue(result.contains("Feedback"));
  }

  @Test
  @DisplayName("Given builder, when building feedback, then all fields set correctly")
  void testBuilder() {
    Feedback builtFeedback =
        Feedback.builder()
            .id(10L)
            .reviewerId(5L)
            .reviewerName("Test Reviewer")
            .revieweeId(3L)
            .revieweeName("Test Reviewee")
            .mentorshipCycleId(2L)
            .feedbackType(FeedbackType.MENTORSHIP_PROGRAM)
            .rating(4)
            .feedbackText("Great program!")
            .year(2026)
            .isAnonymous(true)
            .isApproved(false)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    assertEquals(10L, builtFeedback.getId());
    assertEquals(5L, builtFeedback.getReviewerId());
    assertEquals("Test Reviewer", builtFeedback.getReviewerName());
    assertEquals(3L, builtFeedback.getRevieweeId());
    assertEquals("Test Reviewee", builtFeedback.getRevieweeName());
    assertEquals(2L, builtFeedback.getMentorshipCycleId());
    assertEquals(FeedbackType.MENTORSHIP_PROGRAM, builtFeedback.getFeedbackType());
    assertEquals(4, builtFeedback.getRating());
    assertEquals("Great program!", builtFeedback.getFeedbackText());
    assertEquals(2026, builtFeedback.getYear());
    assertTrue(builtFeedback.getIsAnonymous());
    assertFalse(builtFeedback.getIsApproved());
    assertNotNull(builtFeedback.getCreatedAt());
    assertNotNull(builtFeedback.getUpdatedAt());
  }

  @Test
  @DisplayName("Given builder with null fields, when building, then creates feedback with nulls")
  void testBuilderWithNullFields() {
    Feedback minimalFeedback =
        Feedback.builder()
            .reviewerId(1L)
            .feedbackType(FeedbackType.COMMUNITY_GENERAL)
            .feedbackText("Test")
            .build();

    assertNull(minimalFeedback.getId());
    assertNull(minimalFeedback.getRevieweeId());
    assertNull(minimalFeedback.getMentorshipCycleId());
    assertNull(minimalFeedback.getRating());
    assertNull(minimalFeedback.getYear());
    assertNull(minimalFeedback.getIsAnonymous());
    assertNull(minimalFeedback.getIsApproved());
  }

  @Test
  @DisplayName("Given feedback, when using toBuilder, then creates copy with modifications")
  void testToBuilder() {
    Feedback modifiedFeedback =
        feedback.toBuilder().rating(4).feedbackText("Updated feedback").build();

    assertEquals(feedback.getId(), modifiedFeedback.getId());
    assertEquals(feedback.getReviewerId(), modifiedFeedback.getReviewerId());
    assertEquals(4, modifiedFeedback.getRating());
    assertEquals("Updated feedback", modifiedFeedback.getFeedbackText());
  }

  @Test
  @DisplayName("Given feedback, when setters called, then values updated")
  void testSetters() {
    // Only test setters that are actually available in production code
    feedback.setId(99L);
    feedback.setReviewerName("New Reviewer");
    feedback.setRevieweeName("New Reviewee");
    feedback.setIsAnonymous(false);
    feedback.setIsApproved(true);

    assertEquals(99L, feedback.getId());
    assertEquals("New Reviewer", feedback.getReviewerName());
    assertEquals("New Reviewee", feedback.getRevieweeName());
    assertFalse(feedback.getIsAnonymous());
    assertTrue(feedback.getIsApproved());
  }

  @Test
  @DisplayName("Given no-arg constructor, when created, then all fields null")
  void testNoArgsConstructor() {
    Feedback emptyFeedback = new Feedback();

    assertNull(emptyFeedback.getId());
    assertNull(emptyFeedback.getReviewerId());
    assertNull(emptyFeedback.getReviewerName());
    assertNull(emptyFeedback.getRevieweeId());
    assertNull(emptyFeedback.getRevieweeName());
    assertNull(emptyFeedback.getMentorshipCycleId());
    assertNull(emptyFeedback.getFeedbackType());
    assertNull(emptyFeedback.getRating());
    assertNull(emptyFeedback.getFeedbackText());
    assertNull(emptyFeedback.getYear());
    assertNull(emptyFeedback.getIsAnonymous());
    assertNull(emptyFeedback.getIsApproved());
    assertNull(emptyFeedback.getCreatedAt());
    assertNull(emptyFeedback.getUpdatedAt());
  }

  @Test
  @DisplayName("Given all-args constructor, when created, then all fields set")
  void testAllArgsConstructor() {
    OffsetDateTime now = OffsetDateTime.now();
    Feedback fullFeedback =
        new Feedback(
            1L,
            2L,
            "Reviewer Name",
            3L,
            "Reviewee Name",
            4L,
            FeedbackType.MENTOR_REVIEW,
            5,
            "Feedback text",
            2026,
            true,
            false,
            now,
            now);

    assertEquals(1L, fullFeedback.getId());
    assertEquals(2L, fullFeedback.getReviewerId());
    assertEquals("Reviewer Name", fullFeedback.getReviewerName());
    assertEquals(3L, fullFeedback.getRevieweeId());
    assertEquals("Reviewee Name", fullFeedback.getRevieweeName());
    assertEquals(4L, fullFeedback.getMentorshipCycleId());
    assertEquals(FeedbackType.MENTOR_REVIEW, fullFeedback.getFeedbackType());
    assertEquals(5, fullFeedback.getRating());
    assertEquals("Feedback text", fullFeedback.getFeedbackText());
    assertEquals(2026, fullFeedback.getYear());
    assertTrue(fullFeedback.getIsAnonymous());
    assertFalse(fullFeedback.getIsApproved());
    assertEquals(now, fullFeedback.getCreatedAt());
    assertEquals(now, fullFeedback.getUpdatedAt());
  }

  @Test
  @DisplayName("Given feedback with different rating, when equals called, then returns false")
  void testNotEqualsWithDifferentRating() {
    Feedback feedback1 = feedback.toBuilder().rating(5).build();
    Feedback feedback2 = feedback.toBuilder().rating(3).build();

    assertNotEquals(feedback1, feedback2);
  }

  @Test
  @DisplayName("Given feedback with different anonymous status, when equals, then returns false")
  void testNotEqualsWithDifferentAnonymousStatus() {
    Feedback feedback1 = feedback.toBuilder().isAnonymous(true).build();
    Feedback feedback2 = feedback.toBuilder().isAnonymous(false).build();

    assertNotEquals(feedback1, feedback2);
  }

  @Test
  @DisplayName("Given feedback with different approval status, when equals, then returns false")
  void testNotEqualsWithDifferentApprovalStatus() {
    Feedback feedback1 = feedback.toBuilder().isApproved(true).build();
    Feedback feedback2 = feedback.toBuilder().isApproved(false).build();

    assertNotEquals(feedback1, feedback2);
  }

  @Test
  @DisplayName("Given feedback equals null, when equals called, then returns false")
  void testNotEqualsWithNull() {
    assertNotEquals(null, feedback);
  }

  @Test
  @DisplayName("Given feedback equals different class, when equals called, then returns false")
  void testNotEqualsWithDifferentClass() {
    assertNotEquals("Not a Feedback object", feedback);
  }

  @Test
  @DisplayName("Given same feedback instance, when equals called, then returns true")
  void testEqualsSameInstance() {
    assertEquals(feedback, feedback);
  }
}
