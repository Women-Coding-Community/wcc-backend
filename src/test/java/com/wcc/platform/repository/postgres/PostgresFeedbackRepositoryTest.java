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
import org.junit.jupiter.api.DisplayName;
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
  private static final String GET_ALL_BASE =
      "SELECT f.*, m1.full_name AS reviewer_name, m2.full_name AS reviewee_name "
          + "FROM feedback f "
          + "LEFT JOIN members m1 ON m1.id = f.reviewer_id "
          + "LEFT JOIN members m2 ON m2.id = f.reviewee_id "
          + "WHERE 1 = 1";

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
  @DisplayName("Given valid feedback, when creating, then returns created feedback with ID")
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
  @DisplayName(
      "Given feedback creation with empty findById result, when creating, then throws exception")
  void testCreateThrowsWhenFindByIdEmpty() {
    Feedback feedback = createMentorReviewFeedbackTest();
    when(feedbackMapper.addFeedback(any())).thenReturn(1L);
    doReturn(Optional.empty()).when(repository).findById(1L);

    assertThrows(NoSuchElementException.class, () -> repository.create(feedback));
  }

  @Test
  @DisplayName("Given feedback ID and updated data, when updating, then returns updated feedback")
  void testUpdate() {
    Feedback feedback =
        createMentorReviewFeedbackTest().toBuilder().feedbackText("Updated feedback text").build();
    doNothing().when(feedbackMapper).updateFeedback(any(), anyLong());
    doReturn(Optional.of(feedback)).when(repository).findById(1L);

    Feedback result = repository.update(1L, feedback);

    assertNotNull(result);
    assertEquals("Updated feedback text", result.getFeedbackText());
    verify(feedbackMapper).updateFeedback(feedback, 1L);
  }

  @Test
  @DisplayName("Given valid feedback ID, when finding by ID, then returns feedback")
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
  @DisplayName("Given non-existent feedback ID, when finding by ID, then returns empty")
  void testFindByIdNotFound() {
    Long feedbackId = 999L;
    when(jdbc.query(anyString(), (ResultSetExtractor<Object>) any(), eq(feedbackId)))
        .thenReturn(Optional.empty());

    Optional<Feedback> result = repository.findById(feedbackId);

    assertNotNull(result);
    assertEquals(Optional.empty(), result);
  }

  @Test
  @DisplayName("Given valid feedback ID, when deleting, then executes delete query")
  void testDeleteById() {
    Long feedbackId = 1L;
    when(jdbc.update(DELETE_SQL, feedbackId)).thenReturn(1);

    repository.deleteById(feedbackId);

    verify(jdbc).update(DELETE_SQL, feedbackId);
  }

  @Test
  @DisplayName("Given valid feedback ID, when approving, then executes approve query")
  void testApproveFeedback() {
    Long feedbackId = 1L;
    when(jdbc.update(APPROVE_FEEDBACK, feedbackId)).thenReturn(1);

    repository.approveFeedback(feedbackId);

    verify(jdbc).update(APPROVE_FEEDBACK, feedbackId);
  }

  @Test
  @DisplayName(
      "Given feedback ID and anonymous true, when updating anonymous status, then executes update")
  void testUpdateAnonymousStatus() {
    Long feedbackId = 1L;
    Boolean isAnonymous = true;
    when(jdbc.update(SET_ANONYMOUS_STATUS, isAnonymous, feedbackId)).thenReturn(1);

    repository.updateAnonymousStatus(feedbackId, isAnonymous);

    verify(jdbc).update(SET_ANONYMOUS_STATUS, isAnonymous, feedbackId);
  }

  @Test
  @DisplayName(
      "Given feedback ID and anonymous false, when updating anonymous status, then executes update")
  void testUpdateAnonymousStatusToFalse() {
    Long feedbackId = 1L;
    Boolean isAnonymous = false;
    when(jdbc.update(SET_ANONYMOUS_STATUS, isAnonymous, feedbackId)).thenReturn(1);

    repository.updateAnonymousStatus(feedbackId, isAnonymous);

    verify(jdbc).update(SET_ANONYMOUS_STATUS, isAnonymous, feedbackId);
  }

  @Test
  @DisplayName("Given JDBC throws exception, when finding by ID, then propagates exception")
  void testFindByIdJdbcThrows() {
    Long feedbackId = 1L;
    when(jdbc.query(anyString(), (ResultSetExtractor<Object>) any(), eq(feedbackId)))
        .thenThrow(new RuntimeException("DB error"));

    assertThrows(RuntimeException.class, () -> repository.findById(feedbackId));
  }

  @Test
  @DisplayName(
      "Given invalid search criteria, when getting all, then throws FeedbackNotFoundException")
  void testGetAllThrowsFeedbackNotFoundException() {
    FeedbackSearchCriteria criteria = FeedbackSearchCriteria.builder().reviewerId(1L).build();
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenThrow(
            new com.wcc.platform.domain.exceptions.FeedbackNotFoundException(
                "Invalid search criteria"));

    assertThrows(
        com.wcc.platform.domain.exceptions.FeedbackNotFoundException.class,
        () -> repository.getAll(criteria));
  }

  @Test
  @DisplayName("Given non-existent feedback ID, when deleting, then executes delete query")
  void testDeleteByIdNonExistent() {
    Long feedbackId = 999L;
    when(jdbc.update(DELETE_SQL, feedbackId)).thenReturn(0);

    repository.deleteById(feedbackId);

    verify(jdbc).update(DELETE_SQL, feedbackId);
  }

  @Test
  @DisplayName("Given null criteria, when getting all, then returns all feedback")
  void testGetAllNullCriteria() {
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(java.util.Collections.emptyList());

    var result = repository.getAll(null);

    assertNotNull(result);
    verify(jdbc).query(eq(GET_ALL_BASE), any(RowMapper.class), eq(new Object[0]));
  }

  @Test
  @DisplayName("Given empty criteria, when getting all, then returns empty list")
  void testGetAllEmpty() {
    FeedbackSearchCriteria criteria = FeedbackSearchCriteria.builder().build();
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(java.util.Collections.emptyList());

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(jdbc).query(eq(GET_ALL_BASE), any(RowMapper.class), eq(new Object[0]));
  }

  @Test
  @DisplayName("Given multiple search criteria, when getting all, then returns matching feedback")
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

    when(jdbc.query(
            eq(GET_ALL_BASE + " AND reviewer_id = ? AND reviewee_id = ? AND feedback_year = ?"),
            any(RowMapper.class),
            eq(new Object[] {reviewerId, revieweeId, year})))
        .thenReturn(java.util.List.of(feedback1, feedback2));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(jdbc)
        .query(
            eq(GET_ALL_BASE + " AND reviewer_id = ? AND reviewee_id = ? AND feedback_year = ?"),
            any(RowMapper.class),
            eq(new Object[] {reviewerId, revieweeId, year}));
  }

  @Test
  @DisplayName("Given reviewer ID filter, when getting all, then returns feedback by reviewer")
  void testGetAllWithReviewerIdOnly() {
    Long reviewerId = 1L;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().reviewerId(reviewerId).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq(GET_ALL_BASE + " AND reviewer_id = ?"),
            any(RowMapper.class),
            eq(new Object[] {reviewerId}));
  }

  @Test
  @DisplayName("Given reviewee ID filter, when getting all, then returns feedback by reviewee")
  void testGetAllWithRevieweeIdOnly() {
    Long revieweeId = 2L;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().revieweeId(revieweeId).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq(GET_ALL_BASE + " AND reviewee_id = ?"),
            any(RowMapper.class),
            eq(new Object[] {revieweeId}));
  }

  @Test
  @DisplayName(
      "Given feedback type filter, when getting all, then returns feedback of specified type")
  void testGetAllWithFeedbackTypeOnly() {
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder()
            .feedbackType(com.wcc.platform.domain.platform.type.FeedbackType.MENTOR_REVIEW)
            .build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq(GET_ALL_BASE + " AND feedback_type_id = ?"),
            any(RowMapper.class),
            eq(new Object[] {1}));
  }

  @Test
  @DisplayName("Given year filter, when getting all, then returns feedback for specified year")
  void testGetAllWithYearOnly() {
    Integer year = 2026;
    FeedbackSearchCriteria criteria = FeedbackSearchCriteria.builder().year(year).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq(GET_ALL_BASE + " AND feedback_year = ?"),
            any(RowMapper.class),
            eq(new Object[] {year}));
  }

  @Test
  @DisplayName(
      "Given mentorship cycle ID filter, when getting all, then returns feedback for cycle")
  void testGetAllWithMentorshipCycleIdOnly() {
    Long mentorshipCycleId = 5L;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().mentorshipCycleId(mentorshipCycleId).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq(GET_ALL_BASE + " AND mentorship_cycle_id = ?"),
            any(RowMapper.class),
            eq(new Object[] {mentorshipCycleId}));
  }

  @Test
  @DisplayName("Given isApproved true filter, when getting all, then returns approved feedback")
  void testGetAllWithIsApprovedTrue() {
    Boolean isApproved = true;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().isApproved(isApproved).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq(GET_ALL_BASE + " AND is_approved = ?"),
            any(RowMapper.class),
            eq(new Object[] {isApproved}));
  }

  @Test
  @DisplayName("Given isApproved false filter, when getting all, then returns unapproved feedback")
  void testGetAllWithIsApprovedFalse() {
    Boolean isApproved = false;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().isApproved(isApproved).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq(GET_ALL_BASE + " AND is_approved = ?"),
            any(RowMapper.class),
            eq(new Object[] {isApproved}));
  }

  @Test
  @DisplayName("Given isAnonymous true filter, when getting all, then returns anonymous feedback")
  void testGetAllWithIsAnonymousTrue() {
    Boolean isAnonymous = true;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().isAnonymous(isAnonymous).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq(GET_ALL_BASE + " AND is_anonymous = ?"),
            any(RowMapper.class),
            eq(new Object[] {isAnonymous}));
  }

  @Test
  @DisplayName(
      "Given isAnonymous false filter, when getting all, then returns non-anonymous feedback")
  void testGetAllWithIsAnonymousFalse() {
    Boolean isAnonymous = false;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().isAnonymous(isAnonymous).build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq(GET_ALL_BASE + " AND is_anonymous = ?"),
            any(RowMapper.class),
            eq(new Object[] {isAnonymous}));
  }

  @Test
  @DisplayName(
      "Given all search criteria, when getting all, then returns feedback matching all filters")
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
    when(jdbc.query(
            eq(
                GET_ALL_BASE
                    + " AND reviewer_id = ? AND reviewee_id = ?"
                    + " AND feedback_type_id = ? AND feedback_year = ? AND mentorship_cycle_id = ?"
                    + " AND is_approved = ? AND is_anonymous = ?"),
            any(RowMapper.class),
            eq(
                new Object[] {
                  reviewerId, revieweeId, 1, year, mentorshipCycleId, isApproved, isAnonymous
                })))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    assertEquals(1, result.size());
    verify(jdbc)
        .query(
            eq(
                GET_ALL_BASE
                    + " AND reviewer_id = ? AND reviewee_id = ?"
                    + " AND feedback_type_id = ? AND feedback_year = ? AND mentorship_cycle_id = ?"
                    + " AND is_approved = ? AND is_anonymous = ?"),
            any(RowMapper.class),
            eq(
                new Object[] {
                  reviewerId, revieweeId, 1, year, mentorshipCycleId, isApproved, isAnonymous
                }));
  }

  @Test
  @DisplayName(
      "Given feedback type and year filters, when getting all, then returns matching feedback")
  void testGetAllWithFeedbackTypeAndYear() {
    Integer year = 2026;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder()
            .feedbackType(com.wcc.platform.domain.platform.type.FeedbackType.COMMUNITY_GENERAL)
            .year(year)
            .build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    verify(jdbc)
        .query(
            eq(GET_ALL_BASE + " AND feedback_type_id = ? AND feedback_year = ?"),
            any(RowMapper.class),
            eq(new Object[] {2, year}));
  }

  @Test
  @DisplayName(
      "Given mentorship cycle and approved filters, when getting all,"
          + "then returns matching feedback")
  @SuppressWarnings("unchecked")
  void testGetAllWithMentorshipCycleAndApproved() {
    Long mentorshipCycleId = 10L;
    Boolean isApproved = true;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder()
            .mentorshipCycleId(mentorshipCycleId)
            .isApproved(isApproved)
            .build();

    Feedback feedback = createMentorReviewFeedbackTest();
    when(jdbc.query(anyString(), any(RowMapper.class), any()))
        .thenReturn(java.util.List.of(feedback));

    var result = repository.getAll(criteria);

    assertNotNull(result);
    verify(jdbc)
        .query(
            eq(GET_ALL_BASE + " AND mentorship_cycle_id = ? AND is_approved = ?"),
            any(RowMapper.class),
            eq(new Object[] {mentorshipCycleId, isApproved}));
  }
}
