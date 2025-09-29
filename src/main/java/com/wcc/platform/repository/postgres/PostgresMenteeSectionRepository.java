package com.wcc.platform.repository.postgres;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_ADDITIONAL;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_FOCUS;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_HOURS;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_IDEAL_MENTEE;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_MENTORSHIP_TYPE_ID;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_MONTH;
import static io.swagger.v3.core.util.Constants.COMMA;

import com.wcc.platform.domain.cms.pages.mentorship.Availability;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.MenteeSectionRepository;
import java.time.Month;
import java.util.Arrays;
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

  private static final String SQL_BASE =
      "SELECT ideal_mentee, focus, additional, created_at, updated_at "
          + "FROM mentor_mentee_section WHERE mentor_id = ?";
  private static final String SQL_MENTORSHIP_TYPE =
      "SELECT t.id as mentorship_type_id FROM mentorship_types t LEFT JOIN mentor_mentorship_types mmt "
          + "ON mmt.mentorship_type = t.id WHERE mmt.mentor_id = ?";
  private static final String SQL_AVAILABILITY =
      "SELECT month, hours FROM mentor_availability WHERE mentor_id = ?";

  private final JdbcTemplate jdbc;

  /**
   * Loads the mentee section data for the given mentor id.
   *
   * @param mentorId the mentor id to look up
   * @return an Optional containing the mapped MenteeSection if present, otherwise Optional.empty()
   */
  public Optional<MenteeSection> findByMentorId(final long mentorId) {
    try {
      MenteeSection menteeSection =
          jdbc.queryForObject(
              SQL_BASE,
              (rs, rowNum) ->
                  new MenteeSection(
                      loadMentorshipTypes(mentorId),
                      loadAvailability(mentorId),
                      rs.getString(COLUMN_IDEAL_MENTEE),
                      parseFocus(
                          Optional.ofNullable(rs.getString(COLUMN_FOCUS)).orElse(EMPTY_STRING)),
                      rs.getString(COLUMN_ADDITIONAL)),
              mentorId);
      return Optional.ofNullable(menteeSection);
    } catch (EmptyResultDataAccessException ex) {
      return Optional.empty();
    }
  }

  private List<String> parseFocus(final String focus) {
    return focus.isBlank()
        ? List.of()
        : Arrays.stream(focus.split(COMMA)).map(String::trim).toList();
  }

  private List<MentorshipType> loadMentorshipTypes(final Long mentorId) {
    return jdbc.query(
        SQL_MENTORSHIP_TYPE,
        (rs, rowNum) -> MentorshipType.fromId(rs.getInt(COLUMN_MENTORSHIP_TYPE_ID)),
        mentorId);
  }

  private List<Availability> loadAvailability(final Long mentorId) {
    return jdbc.query(
        SQL_AVAILABILITY,
        (rs, rowNum) ->
            new Availability(Month.of(rs.getInt(COLUMN_MONTH)), rs.getInt(COLUMN_HOURS)),
        mentorId);
  }
}
