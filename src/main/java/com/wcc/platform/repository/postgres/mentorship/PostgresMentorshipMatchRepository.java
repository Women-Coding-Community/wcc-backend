package com.wcc.platform.repository.postgres.mentorship;

import com.wcc.platform.domain.platform.mentorship.MatchStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipMatch;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * PostgreSQL implementation of MentorshipMatchRepository. Manages confirmed mentorship matches in
 * the database.
 */
@Repository
@RequiredArgsConstructor
public class PostgresMentorshipMatchRepository implements MentorshipMatchRepository {

  private static final String SELECT_ALL =
      "SELECT * FROM mentorship_matches ORDER BY created_at DESC";

  private static final String SELECT_BY_ID = "SELECT * FROM mentorship_matches WHERE match_id = ?";

  private static final String SEL_ACTIVE_BY_MENTOR =
      "SELECT * FROM mentorship_matches "
          + "WHERE mentor_id = ? AND match_status = 'active' "
          + "ORDER BY start_date DESC";

  private static final String SEL_ACTIVE_BY_MENTEE =
      "SELECT * FROM mentorship_matches "
          + "WHERE mentee_id = ? AND match_status = 'active' "
          + "LIMIT 1";

  private static final String SELECT_BY_CYCLE =
      "SELECT * FROM mentorship_matches WHERE cycle_id = ? "
          + "ORDER BY match_status, start_date DESC";

  private static final String COUNT_ACTIVE_MENTOR =
      "SELECT COUNT(*) FROM mentorship_matches "
          + "WHERE mentor_id = ? AND cycle_id = ? AND match_status = 'active'";

  private static final String CHECK_MENTEE_MATCHED =
      "SELECT EXISTS(SELECT 1 FROM mentorship_matches "
          + "WHERE mentee_id = ? AND cycle_id = ? AND match_status = 'active')";

  private static final String SELECT_BY_MENTOR =
      "SELECT * FROM mentorship_matches "
          + "WHERE mentor_id = ? AND mentee_id = ? AND cycle_id = ?";

  private final JdbcTemplate jdbc;

  @Override
  public MentorshipMatch create(final MentorshipMatch entity) {
    // TODO: Implement create - not needed for Phase 3
    throw new UnsupportedOperationException("Create not yet implemented");
  }

  @Override
  public MentorshipMatch update(final Long id, final MentorshipMatch entity) {
    // TODO: Implement update - not needed for Phase 3
    throw new UnsupportedOperationException("Update not yet implemented");
  }

  @Override
  public Optional<MentorshipMatch> findById(final Long matchId) {
    return jdbc.query(
        SELECT_BY_ID, rs -> rs.next() ? Optional.of(mapRow(rs)) : Optional.empty(), matchId);
  }

  @Override
  public void deleteById(final Long id) {
    // TODO: Implement delete - not needed for Phase 3
    throw new UnsupportedOperationException("Delete not yet implemented");
  }

  @Override
  public List<MentorshipMatch> findActiveMenteesByMentor(final Long mentorId) {
    return jdbc.query(SEL_ACTIVE_BY_MENTOR, (rs, rowNum) -> mapRow(rs), mentorId);
  }

  @Override
  public Optional<MentorshipMatch> findActiveMentorByMentee(final Long menteeId) {
    return jdbc.query(
        SEL_ACTIVE_BY_MENTEE,
        rs -> rs.next() ? Optional.of(mapRow(rs)) : Optional.empty(),
        menteeId);
  }

  @Override
  public List<MentorshipMatch> findByCycle(final Long cycleId) {
    return jdbc.query(SELECT_BY_CYCLE, (rs, rowNum) -> mapRow(rs), cycleId);
  }

  @Override
  public int countActiveMenteesByMentorAndCycle(final Long mentorId, final Long cycleId) {
    return jdbc.queryForObject(COUNT_ACTIVE_MENTOR, Integer.class, mentorId, cycleId);
  }

  @Override
  public boolean isMenteeMatchedInCycle(final Long menteeId, final Long cycleId) {
    return jdbc.queryForObject(CHECK_MENTEE_MATCHED, Boolean.class, menteeId, cycleId);
  }

  @Override
  public Optional<MentorshipMatch> findByMentorMenteeCycle(
      final Long mentorId, final Long menteeId, final Long cycleId) {
    return jdbc.query(
        SELECT_BY_MENTOR,
        rs -> rs.next() ? Optional.of(mapRow(rs)) : Optional.empty(),
        mentorId,
        menteeId,
        cycleId);
  }

  @Override
  public List<MentorshipMatch> getAll() {
    return jdbc.query(SELECT_ALL, (rs, rowNum) -> mapRow(rs));
  }

  private MentorshipMatch mapRow(final ResultSet rs) throws SQLException {
    return MentorshipMatch.builder()
        .matchId(rs.getLong("match_id"))
        .mentorId(rs.getLong("mentor_id"))
        .menteeId(rs.getLong("mentee_id"))
        .cycleId(rs.getLong("cycle_id"))
        .applicationId(rs.getObject("application_id") != null ? rs.getLong("application_id") : null)
        .status(MatchStatus.fromValue(rs.getString("match_status")))
        .startDate(rs.getDate("start_date").toLocalDate())
        .endDate(rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null)
        .expectedEndDate(
            rs.getDate("expected_end_date") != null
                ? rs.getDate("expected_end_date").toLocalDate()
                : null)
        .sessionFrequency(rs.getString("session_frequency"))
        .totalSessions(rs.getInt("total_sessions"))
        .cancellationReason(rs.getString("cancellation_reason"))
        .cancelledBy(rs.getString("cancelled_by"))
        .cancelledAt(
            rs.getTimestamp("cancelled_at") != null
                ? rs.getTimestamp("cancelled_at").toInstant().atZone(ZoneId.systemDefault())
                : null)
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
