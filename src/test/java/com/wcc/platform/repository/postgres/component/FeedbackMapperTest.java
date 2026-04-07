package com.wcc.platform.repository.postgres.component;

import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COLUMN_CREATED_AT;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COLUMN_FEEDBACK_TEXT;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COLUMN_FEEDBACK_TYPE_ID;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COLUMN_ID;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COLUMN_IS_ANONYMOUS;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COLUMN_IS_APPROVED;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COLUMN_MENTORSHIP_CYCLE_ID;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COLUMN_RATING;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COLUMN_REVIEWEE_ID;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COLUMN_REVIEWER_ID;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COLUMN_UPDATED_AT;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COLUMN_YEAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.feedback.Feedback;
import com.wcc.platform.domain.platform.type.FeedbackType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

/** FeedbackMapperTest class for testing the FeedbackMapper. */
class FeedbackMapperTest {

  @Mock private JdbcTemplate jdbc;
  @Mock private ResultSet resultSet;

  private FeedbackMapper feedbackMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    feedbackMapper = new FeedbackMapper(jdbc);
  }

  @Test
  void testMapRowToFeedback() throws SQLException {
    Long feedbackId = 1L;
    Long reviewerId = 10L;
    Long revieweeId = 20L;
    Long mentorshipCycleId = 5L;
    Integer feedbackTypeId = 1; // MENTOR_REVIEW
    Integer rating = 5;
    String feedbackText = "Excellent mentor!";
    Integer year = 2026;
    Boolean isAnonymous = false;
    Boolean isApproved = true;
    OffsetDateTime createdAt = OffsetDateTime.now();
    OffsetDateTime updatedAt = OffsetDateTime.now();

    when(resultSet.getLong(COLUMN_ID)).thenReturn(feedbackId);
    when(resultSet.getLong(COLUMN_REVIEWER_ID)).thenReturn(reviewerId);
    when(resultSet.getObject(COLUMN_REVIEWEE_ID)).thenReturn(revieweeId);
    when(resultSet.getLong(COLUMN_REVIEWEE_ID)).thenReturn(revieweeId);
    when(resultSet.getObject(COLUMN_MENTORSHIP_CYCLE_ID)).thenReturn(mentorshipCycleId);
    when(resultSet.getLong(COLUMN_MENTORSHIP_CYCLE_ID)).thenReturn(mentorshipCycleId);
    when(resultSet.getInt(COLUMN_FEEDBACK_TYPE_ID)).thenReturn(feedbackTypeId);
    when(resultSet.getObject(COLUMN_RATING)).thenReturn(rating);
    when(resultSet.getInt(COLUMN_RATING)).thenReturn(rating);
    when(resultSet.getString(COLUMN_FEEDBACK_TEXT)).thenReturn(feedbackText);
    when(resultSet.getObject(COLUMN_YEAR)).thenReturn(year);
    when(resultSet.getInt(COLUMN_YEAR)).thenReturn(year);
    when(resultSet.getBoolean(COLUMN_IS_ANONYMOUS)).thenReturn(isAnonymous);
    when(resultSet.getBoolean(COLUMN_IS_APPROVED)).thenReturn(isApproved);
    when(resultSet.getObject(COLUMN_CREATED_AT)).thenReturn(createdAt);
    when(resultSet.getObject(COLUMN_CREATED_AT, OffsetDateTime.class)).thenReturn(createdAt);
    when(resultSet.getObject(COLUMN_UPDATED_AT)).thenReturn(updatedAt);
    when(resultSet.getObject(COLUMN_UPDATED_AT, OffsetDateTime.class)).thenReturn(updatedAt);

    Feedback feedback = feedbackMapper.mapRowToFeedback(resultSet);

    assertNotNull(feedback);
    assertEquals(feedbackId, feedback.getId());
    assertEquals(reviewerId, feedback.getReviewerId());
    assertEquals(revieweeId, feedback.getRevieweeId());
    assertEquals(mentorshipCycleId, feedback.getMentorshipCycleId());
    assertEquals(FeedbackType.MENTOR_REVIEW, feedback.getFeedbackType());
    assertEquals(rating, feedback.getRating());
    assertEquals(feedbackText, feedback.getFeedbackText());
    assertEquals(year, feedback.getYear());
    assertEquals(isAnonymous, feedback.getIsAnonymous());
    assertEquals(isApproved, feedback.getIsApproved());
    assertEquals(createdAt, feedback.getCreatedAt());
    assertEquals(updatedAt, feedback.getUpdatedAt());
    assertNull(feedback.getReviewerName());
    assertNull(feedback.getRevieweeName());
  }

  @Test
  void testMapRowToFeedbackWithNullableFields() throws SQLException {
    Long feedbackId = 2L;
    Long reviewerId = 10L;
    Integer feedbackTypeId = 2; // COMMUNITY_GENERAL
    String feedbackText = "Great community!";
    Boolean isAnonymous = true;
    Boolean isApproved = false;

    when(resultSet.getLong(COLUMN_ID)).thenReturn(feedbackId);
    when(resultSet.getLong(COLUMN_REVIEWER_ID)).thenReturn(reviewerId);
    when(resultSet.getObject(COLUMN_REVIEWEE_ID)).thenReturn(null);
    when(resultSet.getObject(COLUMN_MENTORSHIP_CYCLE_ID)).thenReturn(null);
    when(resultSet.getInt(COLUMN_FEEDBACK_TYPE_ID)).thenReturn(feedbackTypeId);
    when(resultSet.getObject(COLUMN_RATING)).thenReturn(null);
    when(resultSet.getString(COLUMN_FEEDBACK_TEXT)).thenReturn(feedbackText);
    when(resultSet.getObject(COLUMN_YEAR)).thenReturn(null);
    when(resultSet.getBoolean(COLUMN_IS_ANONYMOUS)).thenReturn(isAnonymous);
    when(resultSet.getBoolean(COLUMN_IS_APPROVED)).thenReturn(isApproved);
    when(resultSet.getObject(COLUMN_CREATED_AT)).thenReturn(null);
    when(resultSet.getObject(COLUMN_UPDATED_AT)).thenReturn(null);

    Feedback feedback = feedbackMapper.mapRowToFeedback(resultSet);

    assertNotNull(feedback);
    assertEquals(feedbackId, feedback.getId());
    assertEquals(reviewerId, feedback.getReviewerId());
    assertNull(feedback.getRevieweeId());
    assertNull(feedback.getMentorshipCycleId());
    assertEquals(FeedbackType.COMMUNITY_GENERAL, feedback.getFeedbackType());
    assertNull(feedback.getRating());
    assertEquals(feedbackText, feedback.getFeedbackText());
    assertNull(feedback.getYear());
    assertEquals(isAnonymous, feedback.getIsAnonymous());
    assertEquals(isApproved, feedback.getIsApproved());
    assertNull(feedback.getCreatedAt());
    assertNull(feedback.getUpdatedAt());
  }

  @Test
  void handlesSqlExceptionGracefully() throws Exception {
    when(resultSet.getLong(COLUMN_ID)).thenThrow(SQLException.class);
    assertThrows(SQLException.class, () -> feedbackMapper.mapRowToFeedback(resultSet));
  }

  @Test
  void testAddFeedback() {
    Feedback feedback =
        Feedback.builder()
            .reviewerId(1L)
            .revieweeId(2L)
            .mentorshipCycleId(5L)
            .feedbackType(FeedbackType.MENTOR_REVIEW)
            .rating(5)
            .feedbackText("Excellent mentor!")
            .year(2026)
            .isAnonymous(true)
            .isApproved(false)
            .build();

    when(jdbc.queryForObject("SELECT LASTVAL()", Long.class)).thenReturn(100L);

    Long feedbackId = feedbackMapper.addFeedback(feedback);

    assertEquals(100L, feedbackId);
    verify(jdbc)
        .update(
            "INSERT INTO feedback ("
                + "reviewer_id, reviewee_id, mentorship_cycle_id, feedback_type_id, "
                + "rating, feedback_text, feedback_year, is_anonymous, is_approved) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
            1L,
            2L,
            5L,
            1,
            5,
            "Excellent mentor!",
            2026,
            true,
            false);
    verify(jdbc).queryForObject("SELECT LASTVAL()", Long.class);
  }

  @Test
  void testAddFeedbackWithNullableFields() {
    Feedback feedback =
        Feedback.builder()
            .reviewerId(1L)
            .feedbackType(FeedbackType.COMMUNITY_GENERAL)
            .feedbackText("Great community!")
            .isAnonymous(false)
            .isApproved(true)
            .build();

    when(jdbc.queryForObject("SELECT LASTVAL()", Long.class)).thenReturn(200L);

    Long feedbackId = feedbackMapper.addFeedback(feedback);

    assertEquals(200L, feedbackId);
    verify(jdbc)
        .update(
            "INSERT INTO feedback ("
                + "reviewer_id, reviewee_id, mentorship_cycle_id, feedback_type_id, "
                + "rating, feedback_text, feedback_year, is_anonymous, is_approved) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
            1L,
            null,
            null,
            2,
            null,
            "Great community!",
            null,
            false,
            true);
  }

  @Test
  void testUpdateFeedback() {
    Long feedbackId = 1L;
    Feedback feedback =
        Feedback.builder()
            .reviewerId(1L)
            .revieweeId(2L)
            .mentorshipCycleId(5L)
            .feedbackType(FeedbackType.MENTOR_REVIEW)
            .rating(4)
            .feedbackText("Updated feedback text")
            .year(2026)
            .isAnonymous(false)
            .isApproved(true)
            .build();

    feedbackMapper.updateFeedback(feedback, feedbackId);

    verify(jdbc)
        .update(
            "UPDATE feedback SET "
                + "reviewer_id = ?, reviewee_id = ?, mentorship_cycle_id = ?, "
                + "feedback_type_id = ?, rating = ?, feedback_text = ?, "
                + "feedback_year = ?, is_anonymous = ?, is_approved = ?, "
                + "updated_at = CURRENT_TIMESTAMP "
                + "WHERE id = ?",
            1L,
            2L,
            5L,
            1,
            4,
            "Updated feedback text",
            2026,
            false,
            true,
            feedbackId);
  }

  @Test
  void testUpdateFeedbackWithNullableFields() {
    Long feedbackId = 2L;
    Feedback feedback =
        Feedback.builder()
            .reviewerId(10L)
            .feedbackType(FeedbackType.MENTORSHIP_PROGRAM)
            .feedbackText("Updated program feedback")
            .isAnonymous(true)
            .isApproved(false)
            .build();

    feedbackMapper.updateFeedback(feedback, feedbackId);

    verify(jdbc)
        .update(
            "UPDATE feedback SET "
                + "reviewer_id = ?, reviewee_id = ?, mentorship_cycle_id = ?, "
                + "feedback_type_id = ?, rating = ?, feedback_text = ?, "
                + "feedback_year = ?, is_anonymous = ?, is_approved = ?, "
                + "updated_at = CURRENT_TIMESTAMP "
                + "WHERE id = ?",
            10L,
            null,
            null,
            3,
            null,
            "Updated program feedback",
            null,
            true,
            false,
            feedbackId);
  }
}
