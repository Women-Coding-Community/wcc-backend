package com.wcc.platform.repository.postgres;

import static com.wcc.platform.factories.SetupFeedbackFactories.createMentorReviewFeedbackTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.feedback.Feedback;
import com.wcc.platform.domain.platform.feedback.FeedbackSearchCriteria;
import com.wcc.platform.repository.postgres.component.FeedbackMapper;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

/** PostgresFeedbackRepositoryTest class for testing the PostgresFeedbackRepository. */
@SuppressWarnings("PMD.TooManyMethods")
class PostgresFeedbackRepositoryTest {

  private static final String DELETE_SQL = "DELETE FROM feedback WHERE id = ?";
  private static final String APPROVE_FEEDBACK =
      "UPDATE feedback SET is_approved = true WHERE id = ?";
  private static final String SET_ANONYMOUS_STATUS =
      "UPDATE feedback SET is_anonymous = ? WHERE id = ?";

  private JdbcTemplate jdbc;
  private FeedbackMapper feedbackMapper;
  private PostgresFeedbackRepository repository;

  @BeforeEach
  void setUp() {
    jdbc = mock(JdbcTemplate.class);
    feedbackMapper = mock(FeedbackMapper.class);
    repository = spy(new PostgresFeedbackRepository(jdbc, feedbackMapper));
  }

  @Test
  void testCreate() {
    Feedback feedback = createMentorReviewFeedbackTest();
    when(feedbackMapper.addFeedback(any())).thenReturn(1L);
    doReturn(Optional.of(feedback)).when(repository).findById(1L);

    Feedback result = repository.create(feedback);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("This is a test feedback", result.getFeedbackText());
    verify(feedbackMapper).addFeedback(feedback);
  }

  @Test
  void testCreateThrowsWhenFindByIdEmpty() {
    Feedback feedback = createMentorReviewFeedbackTest();
    when(feedbackMapper.addFeedback(any())).thenReturn(1L);
    doReturn(Optional.empty()).when(repository).findById(1L);

    assertThrows(NoSuchElementException.class, () -> repository.create(feedback));
  }

  @Test
  void testUpdate() {
    Feedback feedback =
        createMentorReviewFeedbackTest().toBuilder().feedbackText("Updated feedback text").build();
    doNothing().when(feedbackMapper).updateFeedback(any(), anyLong());

    Feedback result = repository.update(1L, feedback);

    assertNotNull(result);
    assertEquals("Updated feedback text", result.getFeedbackText());
    verify(feedbackMapper).updateFeedback(feedback, 1L);
  }

  @Test
  void testFindById() {
    Long feedbackId = 1L;
    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), (ResultSetExtractor<Object>) any(), eq(feedbackId)))
        .thenReturn(Optional.of(feedback));

    Optional<Feedback> result = repository.findById(feedbackId);

    assertNotNull(result);
    assertTrue(result.isPresent());
    assertEquals(feedback, result.get());
    assertEquals(1L, result.get().getId());
  }

  @Test
  void testFindByIdNotFound() {
    Long feedbackId = 999L;
    when(jdbc.query(anyString(), (ResultSetExtractor<Object>) any(), eq(feedbackId)))
        .thenReturn(Optional.empty());

    Optional<Feedback> result = repository.findById(feedbackId);

    assertNotNull(result);
    assertEquals(Optional.empty(), result);
  }

  @Test
  void testDeleteById() {
    Long feedbackId = 1L;
    when(jdbc.update(DELETE_SQL, feedbackId)).thenReturn(1);

    repository.deleteById(feedbackId);

    verify(jdbc).update(DELETE_SQL, feedbackId);
  }

  @Test
  void testApproveFeedback() {
    Long feedbackId = 1L;
    when(jdbc.update(APPROVE_FEEDBACK, feedbackId)).thenReturn(1);

    repository.approveFeedback(feedbackId);

    verify(jdbc).update(APPROVE_FEEDBACK, feedbackId);
  }

  @Test
  void testUpdateAnonymousStatus() {
    Long feedbackId = 1L;
    Boolean isAnonymous = true;
    when(jdbc.update(SET_ANONYMOUS_STATUS, isAnonymous, feedbackId)).thenReturn(1);

    repository.updateAnonymousStatus(feedbackId, isAnonymous);

    verify(jdbc).update(SET_ANONYMOUS_STATUS, isAnonymous, feedbackId);
  }

  @Test
  void testUpdateAnonymousStatusToFalse() {
    Long feedbackId = 1L;
    Boolean isAnonymous = false;
    when(jdbc.update(SET_ANONYMOUS_STATUS, isAnonymous, feedbackId)).thenReturn(1);

    repository.updateAnonymousStatus(feedbackId, isAnonymous);

    verify(jdbc).update(SET_ANONYMOUS_STATUS, isAnonymous, feedbackId);
  }

  @Test
  void testFindByIdJdbcThrows() {
    Long feedbackId = 1L;
    when(jdbc.query(anyString(), (ResultSetExtractor<Object>) any(), eq(feedbackId)))
        .thenThrow(new RuntimeException("DB error"));

    assertThrows(RuntimeException.class, () -> repository.findById(feedbackId));
  }

  @Test
  void testGetAllThrowsFeedbackNotFoundException() {
    FeedbackSearchCriteria criteria = FeedbackSearchCriteria.builder().reviewerId(1L).build();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenThrow(
            new com.wcc.platform.domain.exceptions.FeedbackNotFoundException(
                "Invalid search criteria"));

    assertThrows(
        com.wcc.platform.domain.exceptions.FeedbackNotFoundException.class,
        () -> repository.getAll(criteria));
  }

  @Test
  void testDeleteByIdNonExistent() {
    Long feedbackId = 999L;
    when(jdbc.update(DELETE_SQL, feedbackId)).thenReturn(0);

    repository.deleteById(feedbackId);

    verify(jdbc).update(DELETE_SQL, feedbackId);
  }

  @Test
  void testGetAllNullCriteria() {
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.Collections.emptyList());

    var result = repository.getAll(null);

    assertNotNull(result);
    verify(jdbc)
        .query(eq("SELECT * FROM feedback WHERE 1=1"), any(RowMapper.class), eq(new Object[0]));
  }

  @Test
  void testGetAllEmpty() {
    FeedbackSearchCriteria criteria = FeedbackSearchCriteria.builder().build();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.Collections.emptyList());

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(jdbc)
        .query(eq("SELECT * FROM feedback WHERE 1=1"), any(RowMapper.class), eq(new Object[0]));
  }

  @Test
  void testGetAllWithMultipleCriteria() {
    Long reviewerId = 1L;
    Long revieweeId = 2L;
    Integer year = 2026;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder()
            .reviewerId(reviewerId)
            .revieweeId(revieweeId)
            .year(year)
            .build();

    Feedback feedback1 = createMentorReviewFeedbackTest();
    Feedback feedback2 = createMentorReviewFeedbackTest();
    feedback2.setId(2L);

    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.List.of(feedback1, feedback2));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(jdbc)
        .query(
            eq(
                "SELECT * FROM feedback WHERE 1=1 AND reviewer_id = ? AND reviewee_id = ? AND feedback_year = ?"),
            any(RowMapper.class),
            eq(new Object[] {reviewerId, revieweeId, year}));
  }

  @Test
  void testGetAllWithReviewerIdOnly() {
    Long reviewerId = 1L;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().reviewerId(reviewerId).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq("SELECT * FROM feedback WHERE 1=1 AND reviewer_id = ?"),
            any(RowMapper.class),
            eq(new Object[] {reviewerId}));
  }

  @Test
  void testGetAllWithRevieweeIdOnly() {
    Long revieweeId = 2L;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().revieweeId(revieweeId).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq("SELECT * FROM feedback WHERE 1=1 AND reviewee_id = ?"),
            any(RowMapper.class),
            eq(new Object[] {revieweeId}));
  }

  @Test
  void testGetAllWithFeedbackTypeOnly() {
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder()
            .feedbackType(com.wcc.platform.domain.platform.type.FeedbackType.MENTOR_REVIEW)
            .build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq("SELECT * FROM feedback WHERE 1=1 AND feedback_type_id = ?"),
            any(RowMapper.class),
            eq(new Object[] {1}));
  }

  @Test
  void testGetAllWithYearOnly() {
    Integer year = 2026;
    FeedbackSearchCriteria criteria = FeedbackSearchCriteria.builder().year(year).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq("SELECT * FROM feedback WHERE 1=1 AND feedback_year = ?"),
            any(RowMapper.class),
            eq(new Object[] {year}));
  }

  @Test
  void testGetAllWithMentorshipCycleIdOnly() {
    Long mentorshipCycleId = 5L;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().mentorshipCycleId(mentorshipCycleId).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq("SELECT * FROM feedback WHERE 1=1 AND mentorship_cycle_id = ?"),
            any(RowMapper.class),
            eq(new Object[] {mentorshipCycleId}));
  }

  @Test
  void testGetAllWithIsApprovedTrue() {
    Boolean isApproved = true;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().isApproved(isApproved).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq("SELECT * FROM feedback WHERE 1=1 AND is_approved = ?"),
            any(RowMapper.class),
            eq(new Object[] {isApproved}));
  }

  @Test
  void testGetAllWithIsApprovedFalse() {
    Boolean isApproved = false;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().isApproved(isApproved).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq("SELECT * FROM feedback WHERE 1=1 AND is_approved = ?"),
            any(RowMapper.class),
            eq(new Object[] {isApproved}));
  }

  @Test
  void testGetAllWithIsAnonymousTrue() {
    Boolean isAnonymous = true;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().isAnonymous(isAnonymous).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq("SELECT * FROM feedback WHERE 1=1 AND is_anonymous = ?"),
            any(RowMapper.class),
            eq(new Object[] {isAnonymous}));
  }

  @Test
  void testGetAllWithIsAnonymousFalse() {
    Boolean isAnonymous = false;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().isAnonymous(isAnonymous).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq("SELECT * FROM feedback WHERE 1=1 AND is_anonymous = ?"),
            any(RowMapper.class),
            eq(new Object[] {isAnonymous}));
  }

  @Test
  void testGetAllWithAllCriteria() {
    Long reviewerId = 1L;
    Long revieweeId = 2L;
    Long mentorshipCycleId = 3L;
    Integer year = 2026;
    Boolean isApproved = true;
    Boolean isAnonymous = false;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder()
            .reviewerId(reviewerId)
            .revieweeId(revieweeId)
            .feedbackType(com.wcc.platform.domain.platform.type.FeedbackType.MENTOR_REVIEW)
            .year(year)
            .mentorshipCycleId(mentorshipCycleId)
            .isApproved(isApproved)
            .isAnonymous(isAnonymous)
            .build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq(
                "SELECT * FROM feedback WHERE 1=1 AND reviewer_id = ? AND reviewee_id = ?"
                    + " AND feedback_type_id = ? AND feedback_year = ? AND mentorship_cycle_id = ?"
                    + " AND is_approved = ? AND is_anonymous = ?"),
            any(RowMapper.class),
            eq(
                new Object[] {
                  reviewerId, revieweeId, 1, year, mentorshipCycleId, isApproved, isAnonymous
                }));
  }

  @Test
  void testGetAllWithFeedbackTypeAndYear() {
    Integer year = 2026;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder()
            .feedbackType(com.wcc.platform.domain.platform.type.FeedbackType.COMMUNITY_GENERAL)
            .year(year)
            .build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    verify(jdbc)
        .query(
            eq("SELECT * FROM feedback WHERE 1=1 AND feedback_type_id = ? AND feedback_year = ?"),
            any(RowMapper.class),
            eq(new Object[] {2, year}));
  }

  @Test
  void testGetAllWithMentorshipCycleAndApproved() {
    Long mentorshipCycleId = 10L;
    Boolean isApproved = true;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder()
            .mentorshipCycleId(mentorshipCycleId)
            .isApproved(isApproved)
            .build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class)))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    verify(jdbc)
        .query(
            eq("SELECT * FROM feedback WHERE 1=1 AND mentorship_cycle_id = ? AND is_approved = ?"),
            any(RowMapper.class),
            eq(new Object[] {mentorshipCycleId, isApproved}));
  }
}
