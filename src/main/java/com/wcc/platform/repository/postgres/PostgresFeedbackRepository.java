package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.platform.feedback.Feedback;
import com.wcc.platform.domain.platform.feedback.FeedbackSearchCriteria;
import com.wcc.platform.repository.FeedbackRepository;
import com.wcc.platform.repository.postgres.component.FeedbackMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the FeedbackRepository interface for managing Feedback entities using
 * PostgreSQL as the data source. This class interacts with the database using SQL queries and maps
 * the result sets to Feedback objects with the help of FeedbackRowMapper.
 */
@Repository
@Primary
@RequiredArgsConstructor
public class PostgresFeedbackRepository implements FeedbackRepository {

  private static final String DELETE_SQL = "DELETE FROM feedback WHERE id = ?";
  private static final String SELECT_BY_ID =
      "SELECT f.*, m1.full_name AS reviewer_name, m2.full_name AS reviewee_name FROM feedback f "
          + "LEFT JOIN members m1 ON m1.id = f.reviewer_id "
          + "LEFT JOIN members m2 ON m2.id = f.reviewee_id "
          + "WHERE f.id = ?";
  private static final String APPROVE_FEEDBACK =
      "UPDATE feedback SET is_approved = true WHERE id = ?";
  private static final String SET_ANONYMOUS_STATUS =
      "UPDATE feedback SET is_anonymous = ? WHERE id = ?";

  private final JdbcTemplate jdbc;
  private final FeedbackMapper feedbackMapper;

  @Override
  public Feedback create(final Feedback entity) {
    final Long feedbackId = feedbackMapper.addFeedback(entity);
    return findById(feedbackId).orElseThrow();
  }

  @Override
  public Feedback update(final Long id, final Feedback entity) {
    feedbackMapper.updateFeedback(entity, id);
    return findById(id).orElseThrow();
  }

  @Override
  public Optional<Feedback> findById(final Long id) {
    return jdbc.query(
        SELECT_BY_ID,
        rs -> {
          if (rs.next()) {
            return Optional.of(feedbackMapper.mapRowToFeedback(rs));
          }
          return Optional.empty();
        },
        id);
  }

  @Override
  public void deleteById(final Long feedbackId) {
    jdbc.update(DELETE_SQL, feedbackId);
  }

  @Override
  @SuppressWarnings({"PMD.InsufficientStringBufferDeclaration", "PMD.CognitiveComplexity"})
  public List<Feedback> getAll(final FeedbackSearchCriteria criteria) {
    final StringBuilder sql =
        new StringBuilder(
            "SELECT f.*, m1.full_name AS reviewer_name, m2.full_name AS reviewee_name "
                + "FROM feedback f "
                + "LEFT JOIN members m1 ON m1.id = f.reviewer_id "
                + "LEFT JOIN members m2 ON m2.id = f.reviewee_id "
                + "WHERE 1 = 1");
    final List<Object> params = new ArrayList<>();

    if (criteria != null) {
      if (criteria.getReviewerId() != null) {
        sql.append(" AND reviewer_id = ?");
        params.add(criteria.getReviewerId());
      }
      if (criteria.getRevieweeId() != null) {
        sql.append(" AND reviewee_id = ?");
        params.add(criteria.getRevieweeId());
      }
      if (criteria.getFeedbackType() != null) {
        sql.append(" AND feedback_type_id = ?");
        params.add(criteria.getFeedbackType().getTypeId());
      }
      if (criteria.getYear() != null) {
        sql.append(" AND feedback_year = ?");
        params.add(criteria.getYear());
      }
      if (criteria.getMentorshipCycleId() != null) {
        sql.append(" AND mentorship_cycle_id = ?");
        params.add(criteria.getMentorshipCycleId());
      }
      if (criteria.getIsApproved() != null) {
        sql.append(" AND is_approved = ?");
        params.add(criteria.getIsApproved());
      }
      if (criteria.getIsAnonymous() != null) {
        sql.append(" AND is_anonymous = ?");
        params.add(criteria.getIsAnonymous());
      }
    }

    return jdbc.query(
        sql.toString(), (rs, rowNum) -> feedbackMapper.mapRowToFeedback(rs), params.toArray());
  }

  @Override
  public void approveFeedback(final Long feedbackId) {
    jdbc.update(APPROVE_FEEDBACK, feedbackId);
  }

  @Override
  public void updateAnonymousStatus(final Long feedbackId, final Boolean isAnonymous) {
    jdbc.update(SET_ANONYMOUS_STATUS, isAnonymous, feedbackId);
  }
}
