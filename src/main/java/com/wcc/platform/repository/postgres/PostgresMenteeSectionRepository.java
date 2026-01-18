package com.wcc.platform.repository.postgres;

import static com.wcc.platform.repository.postgres.constants.MentorConstants.*;

import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorMonthAvailability;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.MenteeSectionRepository;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository for loading a mentor's MenteeSection from Postgres using JdbcTemplate.
 *
 * <p>Maps results from the following tables: - mentor_mentee_section - mentor_mentorship_types
 * (joined with mentorship_types) - mentor_availability
 */
@Repository
@AllArgsConstructor
public class PostgresMenteeSectionRepository implements MenteeSectionRepository {

  public static final String UPDATE_MENTEE_SECTION =
      "UPDATE mentor_mentee_section SET ideal_mentee = ?, additional = ? WHERE mentor_id = ?";
  public static final String UPDATE_MENTOR_TYPE =
      "UPDATE mentor_mentorship_types SET mentorship_type = ? WHERE mentor_id = ?";
  private static final String UPDATE_AVAILABILITY =
      "UPDATE mentor_availability SET " + "month_num = ?, " + "hours = ? " + "WHERE mentor_id = ?";
  private static final String SQL_BASE =
      "SELECT ideal_mentee, additional, created_at, updated_at "
          + "FROM mentor_mentee_section WHERE mentor_id = ?";
  private static final String SQL_MENTORSHIP_TYPE =
      "SELECT mentorship_type FROM mentor_mentorship_types WHERE mentor_id = ?";
  private static final String SQL_AVAILABILITY =
      "SELECT month_num, hours FROM mentor_availability WHERE mentor_id = ?";
  private static final String INSERT_MENTOR_MENTEE =
      "INSERT INTO mentor_mentee_section (mentor_id, ideal_mentee, additional) VALUES (?, ?, ?)";
  private static final String INSERT_AVAILABILITY =
      "INSERT INTO mentor_availability (mentor_id, month_num, hours) VALUES (?, ?, ?)";
  private static final String INSERT_MENTOR_TYPES =
      "INSERT INTO mentor_mentorship_types (mentor_id, mentorship_type) VALUES (?, ?)";
  private static final String DELETE_MENTOR_TYPES =
      "DELETE FROM mentor_mentorship_types WHERE mentor_id = ?";
  private final JdbcTemplate jdbc;

  /** Inserts the mentee section details for the mentor. */
  public void insertMenteeSection(final MenteeSection menteeSec, final Long memberId) {
    jdbc.update(INSERT_MENTOR_MENTEE, memberId, menteeSec.idealMentee(), menteeSec.additional());
    insertAvailability(menteeSec, memberId);
    insertMentorshipTypes(menteeSec, memberId);
  }

  /**
   * Updates the mentee section for a mentor (updates menteeSection, deletes old records of mentor
   * availability and type and inserts new ones).
   */
  public void updateMenteeSection(final MenteeSection menteeSec, final Long mentorId) {
    jdbc.update(UPDATE_MENTEE_SECTION, menteeSec.idealMentee(), menteeSec.additional(), mentorId);
    jdbc.update(DELETE_MENTOR_TYPES, mentorId);
    insertMentorshipTypes(menteeSec, mentorId);
    updateAvailability(menteeSec, mentorId);
  }

  /**
   * Loads the mentee section data for the given mentor id.
   *
   * @param mentorId the mentor id to look up
   * @return an Optional containing the mapped MenteeSection if present, otherwise Optional.empty()
   */
  @Override
  public Optional<MenteeSection> findByMentorId(final long mentorId) {
    try {
      final MenteeSection menteeSection =
          jdbc.queryForObject(
              SQL_BASE,
              (rs, rowNum) ->
                  new MenteeSection(
                      loadMentorshipTypes(mentorId),
                      loadAvailability(mentorId),
                      rs.getString(COLUMN_IDEAL_MENTEE),
                      rs.getString(COLUMN_ADDITIONAL)),
              mentorId);
      return Optional.ofNullable(menteeSection);
    } catch (EmptyResultDataAccessException ex) {
      return Optional.empty();
    }
  }

  private List<MentorshipType> loadMentorshipTypes(final Long mentorId) {
    return jdbc.query(
        SQL_MENTORSHIP_TYPE,
        (rs, rowNum) -> MentorshipType.fromId(rs.getInt(COL_MENTORSHIP_TYPE)),
        mentorId);
  }

  private List<MentorMonthAvailability> loadAvailability(final Long mentorId) {
    return jdbc.query(
        SQL_AVAILABILITY,
        (rs, rowNum) ->
            new MentorMonthAvailability(Month.of(rs.getInt(COLUMN_MONTH)), rs.getInt(COLUMN_HOURS)),
        mentorId);
  }

  private void insertAvailability(final MenteeSection ms, final Long memberId) {
    for (final MentorMonthAvailability a : ms.availability()) {
      jdbc.update(INSERT_AVAILABILITY, memberId, a.month().getValue(), a.hours());
    }
  }

  private void insertMentorshipTypes(final MenteeSection ms, final Long memberId) {
    for (final MentorshipType mt : ms.mentorshipType()) {
      jdbc.update(INSERT_MENTOR_TYPES, memberId, mt.getMentorshipTypeId());
    }
  }

  private void updateAvailability(final MenteeSection ms, final Long memberId) {
    for (final MentorMonthAvailability a : ms.availability()) {
      jdbc.update(UPDATE_AVAILABILITY, a.month().getValue(), a.hours(), memberId);
    }
  }
}
