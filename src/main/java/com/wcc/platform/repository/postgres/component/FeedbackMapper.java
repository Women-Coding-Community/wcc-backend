package com.wcc.platform.repository.postgres.component;

import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_CREATED_AT;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_FEEDBACK_TEXT;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_FB_TYPE_ID;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_ID;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_IS_ANONYMOUS;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_IS_APPROVED;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_MS_CYCLE_ID;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_RATING;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_REVIEWEE_ID;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_REVIEWEE_NAME;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_REVIEWER_ID;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_REVIEWER_NAME;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_UPDATED_AT;
import static com.wcc.platform.repository.postgres.constants.FeedbackConstants.COL_YEAR;

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
@SuppressWarnings("PMD.TooManyStaticImports")
public class FeedbackMapper {
  /* default */
  static final String INSERT_SQL =
      "INSERT INTO feedback ("
          + "reviewer_id, reviewee_id, mentorship_cycle_id, feedback_type_id, "
          + "rating, feedback_text, feedback_year, is_anonymous, is_approved) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

  /* default */
  static final String UPDATE_SQL =
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
        .id(rs.getLong(COL_ID))
        .reviewerId(rs.getLong(COL_REVIEWER_ID))
        .reviewerName(rs.getString(COL_REVIEWER_NAME))
        .revieweeId(rs.getObject(COL_REVIEWEE_ID) != null ? rs.getLong(COL_REVIEWEE_ID) : null)
        .revieweeName(rs.getString(COL_REVIEWEE_NAME))
        .mentorshipCycleId(
            rs.getObject(COL_MS_CYCLE_ID) != null
                ? rs.getLong(COL_MS_CYCLE_ID)
                : null)
        .feedbackType(FeedbackType.fromId(rs.getInt(COL_FB_TYPE_ID)))
        .rating(rs.getObject(COL_RATING) != null ? rs.getInt(COL_RATING) : null)
        .feedbackText(rs.getString(COL_FEEDBACK_TEXT))
        .year(rs.getObject(COL_YEAR) != null ? rs.getInt(COL_YEAR) : null)
        .isAnonymous(rs.getBoolean(COL_IS_ANONYMOUS))
        .isApproved(rs.getBoolean(COL_IS_APPROVED))
        .createdAt(
            rs.getObject(COL_CREATED_AT) != null
                ? rs.getObject(COL_CREATED_AT, OffsetDateTime.class)
                : null)
        .updatedAt(
            rs.getObject(COL_UPDATED_AT) != null
                ? rs.getObject(COL_UPDATED_AT, OffsetDateTime.class)
                : null)
        .build();
  }

  /** Adds a new feedback to the database and returns the feedback ID. */
  public Long addFeedback(final Feedback feedback) {
    return jdbc.queryForObject(
        INSERT_SQL,
        Long.class,
        feedback.getReviewerId(),
        feedback.getRevieweeId(),
        feedback.getMentorshipCycleId(),
        feedback.getFeedbackType().getTypeId(),
        feedback.getRating(),
        feedback.getFeedbackText(),
        feedback.getYear(),
        feedback.getIsAnonymous(),
        feedback.getIsApproved());
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
