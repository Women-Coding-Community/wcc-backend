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
    Feedback feedback = createMentorReviewFeedbackTest();
    feedback.setFeedbackText("Updated feedback text");
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
  void testSetAnonymousStatus() {
    Long feedbackId = 1L;
    Boolean isAnonymous = true;
    when(jdbc.update(SET_ANONYMOUS_STATUS, isAnonymous, feedbackId)).thenReturn(1);

    repository.setAnonymousStatus(feedbackId, isAnonymous);

    verify(jdbc).update(SET_ANONYMOUS_STATUS, isAnonymous, feedbackId);
  }

  @Test
  void testSetAnonymousStatusToFalse() {
    Long feedbackId = 1L;
    Boolean isAnonymous = false;
    when(jdbc.update(SET_ANONYMOUS_STATUS, isAnonymous, feedbackId)).thenReturn(1);

    repository.setAnonymousStatus(feedbackId, isAnonymous);

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
        .query(
            eq("SELECT * FROM feedback WHERE 1=1"),
            any(org.springframework.jdbc.core.RowMapper.class),
            eq(new Object[0]));
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
}
