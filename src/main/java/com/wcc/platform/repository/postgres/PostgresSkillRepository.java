package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.repository.SkillRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

/**
 * Repository implementation for managing and retrieving skills data from a PostgreSQL database.
 * This class interacts with the database to fetch mentor skills, including years of experience,
 * technical areas, and programming languages. Implements the `SkillRepository` interface.
 */
@Repository
@AllArgsConstructor
public class PostgresSkillRepository implements SkillRepository {

  private static final String SELECT_AREAS =
      "SELECT ta.name FROM mentor_technical_areas mta "
          + "JOIN technical_areas ta ON mta.technical_area_id = ta.id "
          + "WHERE mta.mentor_id = ?";
  private static final String SELECT_YEARS =
      "SELECT years_experience FROM mentors WHERE mentor_id = ?";
  private final JdbcTemplate jdbcTemplate;

  @Override
  public Optional<Skills> findByMentorId(final Long mentorId) {
    try {
      final List<TechnicalArea> areas = buildTechnicalAreas(mentorId);
      final List<Languages> languages = buildLanguages(mentorId);
      final Integer yearsExperience = getYearsExperience(mentorId);

      return Optional.of(
          new Skills(yearsExperience != null ? yearsExperience : 1, areas, languages));
    } catch (EmptyResultDataAccessException ex) {
      return Optional.empty();
    }
  }

  private Integer getYearsExperience(final Long mentorId) {
    return jdbcTemplate.queryForObject(
        SELECT_YEARS, SingleColumnRowMapper.newInstance(Integer.class), mentorId);
  }

  private List<TechnicalArea> buildTechnicalAreas(final Long mentorId) {
    return jdbcTemplate.query(SELECT_AREAS, (rs, rowNum) -> rs.getString(1), mentorId).stream()
        .filter(Objects::nonNull)
        .map(String::trim)
        .map(
            name -> {
              try {
                return TechnicalArea.valueOf(name);
              } catch (IllegalArgumentException e) {
                return null;
              }
            })
        .filter(Objects::nonNull)
        .distinct()
        .toList();
  }

  private List<Languages> buildLanguages(final Long mentorId) {
    return jdbcTemplate
        .query(
            "SELECT l.name FROM mentor_languages ml "
                + "LEFT JOIN languages l ON ml.language_id = l.id "
                + "WHERE ml.mentor_id = ?",
            (rs, rowNum) -> rs.getString(1),
            mentorId)
        .stream()
        .filter(Objects::nonNull)
        .map(String::trim)
        .map(
            name -> {
              try {
                return Languages.fromName(name);
              } catch (IllegalArgumentException e) {
                return null;
              }
            })
        .filter(Objects::nonNull)
        .distinct()
        .toList();
  }
}
