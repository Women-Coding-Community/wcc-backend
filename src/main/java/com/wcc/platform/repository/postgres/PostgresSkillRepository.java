package com.wcc.platform.repository.postgres;

import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_LANGUAGES;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_TECHNICAL_AREAS;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_YEARS_EXP;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.repository.SkillRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository implementation for managing and retrieving skills data from a PostgreSQL database.
 * This class interacts with the database to fetch mentor skills, including years of experience,
 * technical areas, and programming languages. Implements the `SkillRepository` interface.
 */
@Repository
@AllArgsConstructor
public class PostgresSkillRepository implements SkillRepository {

  private static final String SELECT_MENTOR_SKILLS =
      "SELECT m.years_experience AS years_experience, "
          + "  COALESCE(array_agg(DISTINCT ta.name) FILTER (WHERE ta.name IS NOT NULL)"
          + ", ARRAY[]::text[]) AS technical_areas, "
          + "  COALESCE(array_agg(DISTINCT l.name) FILTER (WHERE l.name IS NOT NULL)"
          + ", ARRAY[]::text[]) AS languages "
          + "FROM mentors m "
          + "LEFT JOIN mentor_technical_areas mta ON m.mentor_id = mta.mentor_id "
          + "LEFT JOIN technical_areas ta ON mta.technical_area_id = ta.id "
          + "LEFT JOIN mentor_languages ml ON m.mentor_id = ml.mentor_id "
          + "LEFT JOIN languages l ON ml.language_id = l.id "
          + "WHERE m.mentor_id = ? "
          + "GROUP BY m.mentor_id, m.years_experience";

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Optional<Skills> findByMentorId(final Long mentorId) {
    try {
      Skills skills =
          jdbcTemplate.queryForObject(
              SELECT_MENTOR_SKILLS,
              (rs, rowNum) -> {
                Integer years = rs.getInt(COLUMN_YEARS_EXP);
                List<TechnicalArea> areas = extractTechnicalAreas(rs);
                List<Languages> languages = extractLanguages(rs);

                return new Skills(years, areas, languages);
              },
              mentorId);
      return Optional.ofNullable(skills);
    } catch (EmptyResultDataAccessException ex) {
      return Optional.empty();
    }
  }

  private List<TechnicalArea> extractTechnicalAreas(final ResultSet rs) {
    try {
      Object[] values = (Object[]) rs.getArray(COLUMN_TECHNICAL_AREAS).getArray();
      return Arrays.stream(values)
          .map(Object::toString)
          .map(
              value -> {
                try {
                  return TechnicalArea.valueOf(value.trim());
                } catch (IllegalArgumentException ex) {
                  return null;
                }
              })
          .toList();
    } catch (SQLException e) {
      return List.of();
    }
  }

  private List<Languages> extractLanguages(final ResultSet rs) {
    try {
      Object[] values = (Object[]) rs.getArray(COLUMN_LANGUAGES).getArray();
      return Arrays.stream(values)
          .map(Object::toString)
          .map(String::trim)
          .map(
              name -> {
                try {
                  return Languages.fromName(name);
                } catch (IllegalArgumentException ex) {
                  return null;
                }
              })
          .filter(Objects::nonNull)
          .toList();
    } catch (SQLException e) {
      return List.of();
    }
  }
}
