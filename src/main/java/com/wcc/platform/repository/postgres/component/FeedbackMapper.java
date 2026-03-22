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

import com.wcc.platform.domain.platform.feedback.Feedback;
import com.wcc.platform.domain.platform.type.FeedbackType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Maps database result sets to Feedback domain objects. */
@Component
@RequiredArgsConstructor
public class FeedbackMapper {
  private static final String INSERT_SQL =
      "INSERT INTO feedback ("
          + "reviewer_id, reviewee_id, mentorship_cycle_id, feedback_type_id, "
          + "rating, feedback_text, feedback_year, is_anonymous, is_approved) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

  private static final String UPDATE_SQL =
      "UPDATE feedback SET "
          + "reviewer_id = ?, reviewee_id = ?, mentorship_cycle_id = ?, "
          + "feedback_type_id = ?, rating = ?, feedback_text = ?, "
          + "feedback_year = ?, is_anonymous = ?, is_approved = ?, "
          + "updated_at = CURRENT_TIMESTAMP "
          + "WHERE id = ?";

  private final JdbcTemplate jdbc;

  /** Maps a database row to a Feedback object. */
  public Feedback mapRowToFeedback(final ResultSet rs) throws SQLException {
    return Feedback.builder()
        .id(rs.getLong(COLUMN_ID))
        .reviewerId(rs.getLong(COLUMN_REVIEWER_ID))
        .reviewerName(null)
        .revieweeId(
            rs.getObject(COLUMN_REVIEWEE_ID) != null ? rs.getLong(COLUMN_REVIEWEE_ID) : null)
        .revieweeName(null)
        .mentorshipCycleId(
            rs.getObject(COLUMN_MENTORSHIP_CYCLE_ID) != null
                ? rs.getLong(COLUMN_MENTORSHIP_CYCLE_ID)
                : null)
        .feedbackType(FeedbackType.fromId(rs.getInt(COLUMN_FEEDBACK_TYPE_ID)))
        .rating(rs.getObject(COLUMN_RATING) != null ? rs.getInt(COLUMN_RATING) : null)
        .feedbackText(rs.getString(COLUMN_FEEDBACK_TEXT))
        .year(rs.getObject(COLUMN_YEAR) != null ? rs.getInt(COLUMN_YEAR) : null)
        .isAnonymous(rs.getBoolean(COLUMN_IS_ANONYMOUS))
        .isApproved(rs.getBoolean(COLUMN_IS_APPROVED))
        .createdAt(
            rs.getObject(COLUMN_CREATED_AT) != null
                ? rs.getObject(COLUMN_CREATED_AT, OffsetDateTime.class)
                : null)
        .updatedAt(
            rs.getObject(COLUMN_UPDATED_AT) != null
                ? rs.getObject(COLUMN_UPDATED_AT, OffsetDateTime.class)
                : null)
        .build();
  }

  /** Adds a new feedback to the database and returns the feedback ID. */
  public Long addFeedback(final Feedback feedback) {
    jdbc.update(
        INSERT_SQL,
        feedback.getReviewerId(),
        feedback.getRevieweeId(),
        feedback.getMentorshipCycleId(),
        feedback.getFeedbackType().getTypeId(),
        feedback.getRating(),
        feedback.getFeedbackText(),
        feedback.getYear(),
        feedback.getIsAnonymous(),
        feedback.getIsApproved());

    // Return the last inserted ID
    return jdbc.queryForObject("SELECT LASTVAL()", Long.class);
  }

  /**
   * Updates an existing feedback in the database.
   *
   * @param feedback the feedback entity with updated values
   * @param feedbackId the ID of the feedback to update
   */
  public void updateFeedback(final Feedback feedback, final Long feedbackId) {
    jdbc.update(
        UPDATE_SQL,
        feedback.getReviewerId(),
        feedback.getRevieweeId(),
        feedback.getMentorshipCycleId(),
        feedback.getFeedbackType().getTypeId(),
        feedback.getRating(),
        feedback.getFeedbackText(),
        feedback.getYear(),
        feedback.getIsAnonymous(),
        feedback.getIsApproved(),
        feedbackId);
  }
}
