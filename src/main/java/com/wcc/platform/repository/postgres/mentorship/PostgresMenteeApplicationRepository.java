package com.wcc.platform.repository.postgres.mentorship;

import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.repository.MenteeApplicationRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

  private static final String SEL_PENDING_MENTEE =
      "SELECT * FROM mentee_applications WHERE mentee_id = ? "
          + "AND application_status = 'pending' ORDER BY priority_order";

  private static final String SEL_BY_MENTOR =
      "SELECT * FROM mentee_applications "
          + "WHERE mentee_id = ? AND mentor_id = ? AND cycle_id = ?";

  private static final String UPDATE_STATUS =
      "UPDATE mentee_applications SET application_status = ?::application_status, "
          + "mentor_response = ?, updated_at = CURRENT_TIMESTAMP "
          + "WHERE application_id = ?";

  private static final String INSERT_APPLICATION =
      "INSERT INTO mentee_applications "
          + "(mentee_id, mentor_id, cycle_id, priority_order, "
          + "application_status, application_message, why_mentor) "
          + "VALUES (?, ?, ?, ?, ?::application_status, ?, ?) "
          + "RETURNING application_id";

  private static final String SEL_BY_MNTE_CYC_STS =
      "SELECT * FROM mentee_applications "
          + "WHERE mentee_id = ? AND cycle_id = ? "
          + "AND application_status = ?::application_status "
          + "ORDER BY priority_order";

  private static final String SEL_BY_STS_CYC =
      "SELECT * FROM mentee_applications "
          + "WHERE application_status = ?::application_status "
          + "AND cycle_id = ?";

  private static final String SQL_CYCLE_STATUSES =
      "SELECT * FROM mentee_applications WHERE cycle_id = :cycleId "
          + "AND application_status::text IN (:statuses) "
          + "ORDER BY applied_at DESC";

  private static final String SQL_STATUSES_MENTOR =
      "SELECT * FROM mentee_applications WHERE cycle_id = :cycleId "
          + "AND application_status::text IN (:statuses) "
          + "AND mentor_id = :mentorId "
          + "ORDER BY applied_at DESC";

  private static final String DELETE_BY_ID =
      "DELETE FROM mentee_applications WHERE application_id = ?";

  private final JdbcTemplate jdbc;
  private final NamedParameterJdbcTemplate namedJdbc;

  @Transactional
  @Override
  public MenteeApplication create(final MenteeApplication entity) {
    if (entity.getMentorId() != null) {
      final var existing =
          findByMenteeMentorCycle(entity.getMenteeId(), entity.getMentorId(), entity.getCycleId());
      if (existing.isPresent()) {
        return existing.get();
      }
    } else {
      final var existingApps =
          findByMenteeCycleAndStatusOrderByPriority(
              entity.getMenteeId(), entity.getCycleId(), ApplicationStatus.PENDING_MANUAL_MATCH);
      final Optional<MenteeApplication> pendingManualMatch = existingApps.stream().findFirst();
      if (pendingManualMatch.isPresent()) {
        return pendingManualMatch.get();
      }
    }

    final Long generatedId =
        jdbc.query(
            INSERT_APPLICATION,
            rs -> {
              if (rs.next()) {
                return rs.getLong(1);
              }
              return null;
            },
            entity.getMenteeId(),
            entity.getMentorId(),
            entity.getCycleId(),
            entity.getPriorityOrder(),
            entity.getStatus().getValue(),
            entity.getApplicationMessage(),
            entity.getWhyMentor());

    if (generatedId == null) {
      throw new IllegalStateException("Failed to insert application and retrieve ID");
    }

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
    jdbc.update(DELETE_BY_ID, id);
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

  @Override
  public List<MenteeApplication> findPendingByMenteeId(final Long menteeId) {
    return jdbc.query(SEL_PENDING_MENTEE, (rs, rowNum) -> mapRow(rs), menteeId);
  }

  @Override
  public List<MenteeApplication> findByMenteeCycleAndStatusOrderByPriority(
      final Long menteeId, final Long cycleId, final ApplicationStatus status) {
    return jdbc.query(
        SEL_BY_MNTE_CYC_STS, (rs, rowNum) -> mapRow(rs), menteeId, cycleId, status.getValue());
  }

  @Override
  public List<MenteeApplication> findByStatusAndCycle(
      final ApplicationStatus status, final Long cycleId) {
    return jdbc.query(SEL_BY_STS_CYC, (rs, rowNum) -> mapRow(rs), status.getValue(), cycleId);
  }

  @Override
  @Cacheable(value = "menteeApplications", key = "{#cycleId, #statuses}")
  public List<MenteeApplication> findByCycleAndStatuses(
      final Long cycleId, final List<ApplicationStatus> statuses) {
    final MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("cycleId", cycleId);
    params.addValue("statuses", statuses.stream().map(s -> s.name().toLowerCase(Locale.ROOT)).toList());

    return namedJdbc.query(SQL_CYCLE_STATUSES, params, (rs, rowNum) -> mapRow(rs));
  }

  @Override
  @Cacheable(value = "menteeApplications", key = "{#cycleId, #statuses, #mentorId}")
  public List<MenteeApplication> findByCycleAndStatusesAndMentor(
      final Long cycleId, final List<ApplicationStatus> statuses, final Long mentorId) {
    final MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("cycleId", cycleId);
    params.addValue("statuses", statuses.stream().map(s -> s.name().toLowerCase(Locale.ROOT)).toList());
    params.addValue("mentorId", mentorId);

    return namedJdbc.query(SQL_STATUSES_MENTOR, params, (rs, rowNum) -> mapRow(rs));
  }

  private MenteeApplication mapRow(final ResultSet rs) throws SQLException {
    final Long mentorId = rs.getObject("mentor_id") != null ? rs.getLong("mentor_id") : null;
    final Integer priorityOrder =
        rs.getObject("priority_order") != null ? rs.getInt("priority_order") : null;

    return MenteeApplication.builder()
        .applicationId(rs.getLong("application_id"))
        .menteeId(rs.getLong("mentee_id"))
        .mentorId(mentorId)
        .cycleId(rs.getLong("cycle_id"))
        .priorityOrder(priorityOrder)
        .status(ApplicationStatus.fromValue(rs.getString("application_status")))
        .applicationMessage(rs.getString("application_message"))
        .whyMentor(rs.getString("why_mentor"))
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
