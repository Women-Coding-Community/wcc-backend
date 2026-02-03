package com.wcc.platform.repository.postgres.mentorship;

import static com.wcc.platform.repository.postgres.constants.MentorConstants.*;

import com.wcc.platform.domain.cms.pages.mentorship.LongTermMentorship;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorMonthAvailability;
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
 * <p>Maps results from the following tables: - mentor_mentee_section (includes long-term data) -
 * mentor_availability (for ad-hoc availability)
 */
@Repository
@AllArgsConstructor
public class PostgresMenteeSectionRepository implements MenteeSectionRepository {

  private static final String SQL_BASE =
      "SELECT ideal_mentee, additional, long_term_num_mentee, long_term_hours, "
          + "created_at, updated_at FROM mentor_mentee_section WHERE mentor_id = ?";

  private static final String SQL_AD_HOC =
      "SELECT month_num, hours FROM mentor_availability WHERE mentor_id = ?";

  private static final String INSERT_MENTOR_MENTEE =
      "INSERT INTO mentor_mentee_section "
          + "(mentor_id, ideal_mentee, additional, long_term_num_mentee, long_term_hours) "
          + "VALUES (?, ?, ?, ?, ?)";

  private static final String INSERT_AD_HOC =
      "INSERT INTO mentor_availability (mentor_id, month_num, hours) VALUES (?, ?, ?)";

  private static final String UPDATE_MENTEE_SECTION =
      "UPDATE mentor_mentee_section "
          + "SET ideal_mentee = ?, additional = ?, long_term_num_mentee = ?, long_term_hours = ? "
          + "WHERE mentor_id = ?";

  private static final String DELETE_AD_HOC = "DELETE FROM mentor_availability WHERE mentor_id = ?";

  private final JdbcTemplate jdbc;

  /** Inserts the mentee section details for the mentor. */
  public void insertMenteeSection(final MenteeSection menteeSec, final Long memberId) {
    final var longTerm = menteeSec.longTerm();
    jdbc.update(
        INSERT_MENTOR_MENTEE,
        memberId,
        menteeSec.idealMentee(),
        menteeSec.additional(),
        longTerm != null ? longTerm.numMentee() : null,
        longTerm != null ? longTerm.hours() : null);

    insertAdHocAvailability(menteeSec, memberId);
  }

  /**
   * Updates the mentee section for a mentor. Replaces ad-hoc availability records.
   *
   * @param menteeSec the updated mentee section
   * @param mentorId the mentor's ID
   */
  public void updateMenteeSection(final MenteeSection menteeSec, final Long mentorId) {
    final var longTerm = menteeSec.longTerm();
    jdbc.update(
        UPDATE_MENTEE_SECTION,
        menteeSec.idealMentee(),
        menteeSec.additional(),
        longTerm != null ? longTerm.numMentee() : null,
        longTerm != null ? longTerm.hours() : null,
        mentorId);

    // Update ad-hoc availability
    jdbc.update(DELETE_AD_HOC, mentorId);
    insertAdHocAvailability(menteeSec, mentorId);
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
              (rs, rowNum) -> {
                final Integer numMentee = getInteger(rs, COLUMN_LT_NUM_MENTEE);
                final Integer hours = getInteger(rs, COLUMN_LT_HOURS);
                final LongTermMentorship longTerm =
                    (numMentee != null && hours != null)
                        ? new LongTermMentorship(numMentee, hours)
                        : null;

                return new MenteeSection(
                    rs.getString(COLUMN_IDEAL_MENTEE),
                    rs.getString(COLUMN_ADDITIONAL),
                    longTerm,
                    loadAdHocAvailability(mentorId));
              },
              mentorId);
      return Optional.ofNullable(menteeSection);
    } catch (EmptyResultDataAccessException ex) {
      return Optional.empty();
    }
  }

  private Integer getInteger(final java.sql.ResultSet rs, final String column)
      throws java.sql.SQLException {
    final int value = rs.getInt(column);
    return rs.wasNull() ? null : value;
  }

  private List<MentorMonthAvailability> loadAdHocAvailability(final Long mentorId) {
    return jdbc.query(
        SQL_AD_HOC,
        (rs, rowNum) ->
            new MentorMonthAvailability(Month.of(rs.getInt(COLUMN_MONTH)), rs.getInt(COLUMN_HOURS)),
        mentorId);
  }

  private void insertAdHocAvailability(final MenteeSection ms, final Long memberId) {
    if (ms.adHoc() != null) {
      for (final MentorMonthAvailability a : ms.adHoc()) {
        jdbc.update(INSERT_AD_HOC, memberId, a.month().getValue(), a.hours());
      }
    }
  }
}
