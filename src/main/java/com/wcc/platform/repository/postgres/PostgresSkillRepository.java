package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
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

  private static final String SELECT_AREAS_IDS =
      "SELECT technical_area_id FROM mentor_technical_areas WHERE mentor_id = ?";

  private static final String SELECT_FOCUS_IDS =
      "SELECT focus_area_id FROM mentor_mentorship_focus_areas WHERE mentor_id = ?";

  private static final String SELECT_LANGUAGES_IDS =
      "SELECT language_id FROM mentor_languages WHERE mentor_id = ?";

  private static final String SELECT_YEARS =
      "SELECT years_experience FROM mentors WHERE mentor_id = ?";
  private final JdbcTemplate jdbcTemplate;

  @Override
  public Optional<Skills> findSkills(final Long mentorId) {
    try {
      final var areas = getMentorAreas(mentorId);
      final var focusAreas = getMentorFocusAreas(mentorId);
      final var languages = getMentorLanguages(mentorId);
      final var yearsExperience = getYearsExperience(mentorId);

      return Optional.of(new Skills(yearsExperience, areas, languages, focusAreas));
    } catch (EmptyResultDataAccessException ex) {
      return Optional.empty();
    }
  }

  private Integer getYearsExperience(final Long mentorId) {
    final var yearsExperience =
        jdbcTemplate.queryForObject(
            SELECT_YEARS, SingleColumnRowMapper.newInstance(Integer.class), mentorId);

    return yearsExperience != null ? yearsExperience : 1;
  }

  private List<TechnicalArea> getMentorAreas(final Long mentorId) {
    return jdbcTemplate.query(SELECT_AREAS_IDS, (rs, rowNum) -> rs.getInt(1), mentorId).stream()
        .filter(Objects::nonNull)
        .map(
            areaId -> {
              try {
                return TechnicalArea.fromId(areaId);
              } catch (IllegalArgumentException e) {
                return null;
              }
            })
        .filter(Objects::nonNull)
        .toList();
  }

  private List<MentorshipFocusArea> getMentorFocusAreas(final Long mentorId) {
    return jdbcTemplate.query(SELECT_FOCUS_IDS, (rs, rowNum) -> rs.getInt(1), mentorId).stream()
        .filter(Objects::nonNull)
        .map(
            focusAreaId -> {
              try {
                return MentorshipFocusArea.fromId(focusAreaId);
              } catch (IllegalArgumentException e) {
                return null;
              }
            })
        .filter(Objects::nonNull)
        .toList();
  }

  private List<Languages> getMentorLanguages(final Long mentorId) {
    return jdbcTemplate.query(SELECT_LANGUAGES_IDS, (rs, rowNum) -> rs.getInt(1), mentorId).stream()
        .filter(Objects::nonNull)
        .map(
            name -> {
              try {
                return Languages.fromId(name);
              } catch (IllegalArgumentException e) {
                return null;
              }
            })
        .filter(Objects::nonNull)
        .distinct()
        .toList();
  }
}
