package com.wcc.platform.repository.postgres.mentorship;

import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.MentorshipCycleRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * PostgreSQL implementation of MentorshipCycleRepository. Manages mentorship cycle configuration in
 * the database.
 */
@Repository
@RequiredArgsConstructor
public class PostgresMentorshipCycleRepository implements MentorshipCycleRepository {
  private static final String DELETE_SQL = "DELETE FROM mentorship_cycles WHERE cycle_id = ?";

  private static final String SELECT_ALL =
      "SELECT * FROM mentorship_cycles ORDER BY cycle_year DESC, cycle_month";

  private static final String SELECT_BY_ID = "SELECT * FROM mentorship_cycles WHERE cycle_id = ?";

  private static final String SELECT_OPEN_CYCLE =
      "SELECT * FROM mentorship_cycles WHERE status = 'open' "
          + "AND CURRENT_DATE BETWEEN registration_start_date AND registration_end_date "
          + "LIMIT 1";

  private static final String SEL_BY_YEAR_TYPE =
      "SELECT * FROM mentorship_cycles WHERE cycle_year = ? AND mentorship_type = ?";

  private static final String SELECT_BY_STATUS =
      "SELECT * FROM mentorship_cycles WHERE status = ?::cycle_status ORDER BY cycle_year DESC, cycle_month";

  private static final String SELECT_BY_YEAR =
      "SELECT * FROM mentorship_cycles WHERE cycle_year = ? ORDER BY cycle_month";

  private static final String INSERT_CYCLE =
      "INSERT INTO mentorship_cycles "
          + "(cycle_year, mentorship_type, cycle_month, registration_start_date, "
          + "registration_end_date, cycle_start_date, cycle_end_date, status, "
          + "max_mentees_per_mentor, description) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?::cycle_status, ?, ?) "
          + "RETURNING cycle_id";

  private static final String UPDATE_CYCLE =
      "UPDATE mentorship_cycles SET "
          + "cycle_year = ?, mentorship_type = ?, cycle_month = ?, "
          + "registration_start_date = ?, registration_end_date = ?, "
          + "cycle_start_date = ?, cycle_end_date = ?, "
          + "status = ?::cycle_status, max_mentees_per_mentor = ?, "
          + "description = ?, updated_at = CURRENT_TIMESTAMP "
          + "WHERE cycle_id = ?";

  private final JdbcTemplate jdbc;

  @Override
  public MentorshipCycleEntity create(final MentorshipCycleEntity entity) {
    final Long generatedId =
        jdbc.queryForObject(
            INSERT_CYCLE,
            Long.class,
            entity.getCycleYear().getValue(),
            entity.getMentorshipType().getMentorshipTypeId(),
            entity.getCycleMonth().getValue(),
            entity.getRegistrationStartDate(),
            entity.getRegistrationEndDate(),
            entity.getCycleStartDate(),
            entity.getCycleEndDate(),
            entity.getStatus().getValue(),
            entity.getMaxMenteesPerMentor(),
            entity.getDescription());

    return findById(generatedId)
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Failed to retrieve created cycle with ID: " + generatedId));
  }

  @Override
  public MentorshipCycleEntity update(final Long id, final MentorshipCycleEntity entity) {
    final int rowsUpdated =
        jdbc.update(
            UPDATE_CYCLE,
            entity.getCycleYear().getValue(),
            entity.getMentorshipType().getMentorshipTypeId(),
            entity.getCycleMonth().getValue(),
            entity.getRegistrationStartDate(),
            entity.getRegistrationEndDate(),
            entity.getCycleStartDate(),
            entity.getCycleEndDate(),
            entity.getStatus().getValue(),
            entity.getMaxMenteesPerMentor(),
            entity.getDescription(),
            id);

    if (rowsUpdated == 0) {
      throw new IllegalStateException("Failed to update cycle with ID: " + id);
    }

    return findById(id)
        .orElseThrow(() -> new IllegalStateException("Failed to retrieve updated cycle"));
  }

  @Override
  public Optional<MentorshipCycleEntity> findById(final Long cycleId) {
    return jdbc.query(
        SELECT_BY_ID, rs -> rs.next() ? Optional.of(mapRow(rs)) : Optional.empty(), cycleId);
  }

  @Override
  public void deleteById(final Long id) {
    jdbc.update(DELETE_SQL, id);
  }

  @Override
  public Optional<MentorshipCycleEntity> findOpenCycle() {
    return jdbc.query(
        SELECT_OPEN_CYCLE, rs -> rs.next() ? Optional.of(mapRow(rs)) : Optional.empty());
  }

  @Override
  public Optional<MentorshipCycleEntity> findByYearAndType(
      final Year year, final MentorshipType type) {
    return jdbc.query(
        SEL_BY_YEAR_TYPE,
        rs -> rs.next() ? Optional.of(mapRow(rs)) : Optional.empty(),
        year.getValue(),
        type.getMentorshipTypeId());
  }

  @Override
  public List<MentorshipCycleEntity> findByStatus(final CycleStatus status) {
    return jdbc.query(SELECT_BY_STATUS, (rs, rowNum) -> mapRow(rs), status.getValue());
  }

  @Override
  public List<MentorshipCycleEntity> findByYear(final Integer year) {
    return jdbc.query(SELECT_BY_YEAR, (rs, rowNum) -> mapRow(rs), year);
  }

  @Override
  public List<MentorshipCycleEntity> getAll() {
    return jdbc.query(SELECT_ALL, (rs, rowNum) -> mapRow(rs));
  }

  private MentorshipCycleEntity mapRow(final ResultSet rs) throws SQLException {
    return MentorshipCycleEntity.builder()
        .cycleId(rs.getLong("cycle_id"))
        .cycleYear(Year.of(rs.getInt("cycle_year")))
        .mentorshipType(MentorshipType.fromId(rs.getInt("mentorship_type")))
        .cycleMonth(Month.of(rs.getInt("cycle_month")))
        .registrationStartDate(rs.getDate("registration_start_date").toLocalDate())
        .registrationEndDate(rs.getDate("registration_end_date").toLocalDate())
        .cycleStartDate(rs.getDate("cycle_start_date").toLocalDate())
        .cycleEndDate(
            rs.getDate("cycle_end_date") != null
                ? rs.getDate("cycle_end_date").toLocalDate()
                : null)
        .status(CycleStatus.fromValue(rs.getString("status")))
        .maxMenteesPerMentor(rs.getInt("max_mentees_per_mentor"))
        .description(rs.getString("description"))
        .createdAt(rs.getTimestamp("created_at").toInstant().atZone(ZoneId.systemDefault()))
        .updatedAt(rs.getTimestamp("updated_at").toInstant().atZone(ZoneId.systemDefault()))
        .build();
  }
}
