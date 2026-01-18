package com.wcc.platform.repository.postgres.mentorship;

import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.repository.MenteeApplicationRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * PostgreSQL implementation of MenteeApplicationRepository. Manages mentee applications to mentors
 * in the database.
 */
@Repository
@RequiredArgsConstructor
@SuppressWarnings("PMD.TooManyMethods")
public class PostgresMenteeApplicationRepository implements MenteeApplicationRepository {

  private static final String SELECT_ALL =
      "SELECT * FROM mentee_applications ORDER BY applied_at DESC";

  private static final String SELECT_BY_ID =
      "SELECT * FROM mentee_applications WHERE application_id = ?";

  private static final String SEL_BY_MENTEE_PRIO =
      "SELECT * FROM mentee_applications WHERE mentee_id = ? AND cycle_id = ? "
          + "ORDER BY priority_order";

  private static final String COUNT_MENTEE_APPS =
      "SELECT COUNT(mentee_id) FROM mentee_applications WHERE mentee_id = ? AND cycle_id = ?";

  private static final String SEL_BY_MENTOR_PRIO =
      "SELECT * FROM mentee_applications WHERE mentor_id = ? "
          + "ORDER BY priority_order, applied_at DESC";

  private static final String SELECT_BY_STATUS =
      "SELECT * FROM mentee_applications WHERE application_status = ?::application_status "
          + "ORDER BY applied_at DESC";

  private static final String SEL_BY_MENTOR =
      "SELECT * FROM mentee_applications "
          + "WHERE mentee_id = ? AND mentor_id = ? AND cycle_id = ?";

  private static final String UPDATE_STATUS =
      "UPDATE mentee_applications SET application_status = ?::application_status, "
          + "mentor_response = ?, updated_at = CURRENT_TIMESTAMP "
          + "WHERE application_id = ?";

  private static final String INSERT_APPLICATION =
      "INSERT INTO mentee_applications "
          + "(mentee_id, mentor_id, cycle_id, priority_order, application_status, application_message) "
          + "VALUES (?, ?, ?, ?, ?::application_status, ?) "
          + "RETURNING application_id";

  private final JdbcTemplate jdbc;

  @Override
  public MenteeApplication create(final MenteeApplication entity) {
    final Long generatedId =
        jdbc.queryForObject(
            INSERT_APPLICATION,
            Long.class,
            entity.getMenteeId(),
            entity.getMentorId(),
            entity.getCycleId(),
            entity.getPriorityOrder(),
            entity.getStatus().getValue(),
            entity.getApplicationMessage());

    return findById(generatedId)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Failed to retrieve created application with ID: " + generatedId));
  }

  @Override
  public MenteeApplication update(final Long id, final MenteeApplication entity) {
    // TODO: Implement update - not needed for Phase 3
    throw new UnsupportedOperationException("Update not yet implemented");
  }

  @Override
  public Optional<MenteeApplication> findById(final Long applicationId) {
    return jdbc.query(
        SELECT_BY_ID, rs -> rs.next() ? Optional.of(mapRow(rs)) : Optional.empty(), applicationId);
  }

  @Override
  public void deleteById(final Long id) {
    throw new UnsupportedOperationException("Delete not yet implemented");
  }

  @Override
  public List<MenteeApplication> findByMenteeAndCycle(final Long menteeId, final Long cycleId) {
    return jdbc.query(SEL_BY_MENTEE_PRIO, (rs, rowNum) -> mapRow(rs), menteeId, cycleId);
  }

  @Override
  public List<MenteeApplication> findByMentor(final Long mentorId) {
    return jdbc.query(SEL_BY_MENTOR_PRIO, (rs, rowNum) -> mapRow(rs), mentorId);
  }

  @Override
  public List<MenteeApplication> findByStatus(final ApplicationStatus status) {
    return jdbc.query(SELECT_BY_STATUS, (rs, rowNum) -> mapRow(rs), status.getValue());
  }

  @Override
  public Optional<MenteeApplication> findByMenteeMentorCycle(
      final Long menteeId, final Long mentorId, final Long cycleId) {
    return jdbc.query(
        SEL_BY_MENTOR,
        rs -> rs.next() ? Optional.of(mapRow(rs)) : Optional.empty(),
        menteeId,
        mentorId,
        cycleId);
  }

  @Override
  public List<MenteeApplication> findByMenteeAndCycleOrderByPriority(
      final Long menteeId, final Long cycleId) {
    return jdbc.query(SEL_BY_MENTEE_PRIO, (rs, rowNum) -> mapRow(rs), menteeId, cycleId);
  }

  @Override
  public MenteeApplication updateStatus(
      final Long applicationId, final ApplicationStatus newStatus, final String notes) {
    jdbc.update(UPDATE_STATUS, newStatus.getValue(), notes, applicationId);
    return findById(applicationId)
        .orElseThrow(
            () ->
                new IllegalStateException("Application not found after update: " + applicationId));
  }

  @Override
  public List<MenteeApplication> getAll() {
    return jdbc.query(SELECT_ALL, (rs, rowNum) -> mapRow(rs));
  }

  @Override
  public Long countMenteeApplications(final Long menteeId, final Long cycleId) {
    return jdbc.queryForObject(COUNT_MENTEE_APPS, Long.class, menteeId, cycleId);
  }

  private MenteeApplication mapRow(final ResultSet rs) throws SQLException {
    return MenteeApplication.builder()
        .applicationId(rs.getLong("application_id"))
        .menteeId(rs.getLong("mentee_id"))
        .mentorId(rs.getLong("mentor_id"))
        .cycleId(rs.getLong("cycle_id"))
        .priorityOrder(rs.getInt("priority_order"))
        .status(ApplicationStatus.fromValue(rs.getString("application_status")))
        .applicationMessage(rs.getString("application_message"))
        .appliedAt(
            rs.getTimestamp("applied_at") != null
                ? rs.getTimestamp("applied_at").toInstant().atZone(ZoneId.systemDefault())
                : null)
        .reviewedAt(
            rs.getTimestamp("reviewed_at") != null
                ? rs.getTimestamp("reviewed_at").toInstant().atZone(ZoneId.systemDefault())
                : null)
        .matchedAt(
            rs.getTimestamp("matched_at") != null
                ? rs.getTimestamp("matched_at").toInstant().atZone(ZoneId.systemDefault())
                : null)
        .mentorResponse(rs.getString("mentor_response"))
        .createdAt(
            rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toInstant().atZone(ZoneId.systemDefault())
                : null)
        .updatedAt(
            rs.getTimestamp("updated_at") != null
                ? rs.getTimestamp("updated_at").toInstant().atZone(ZoneId.systemDefault())
                : null)
        .build();
  }
}
