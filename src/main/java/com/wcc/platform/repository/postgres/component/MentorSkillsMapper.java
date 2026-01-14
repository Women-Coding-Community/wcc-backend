package com.wcc.platform.repository.postgres.component;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.Skills;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Utility class for inserting mentor skills data. Maps database rows to Mentor domain objects. */
@Slf4j
@Component
public final class MentorSkillsMapper {
  private static final String SQL_TECH_AREAS_INSERT =
      "INSERT INTO mentor_technical_areas (mentor_id, technical_area_id) VALUES (?, ?)";
  private static final String SQL_PROG_LANG_INSERT =
      "INSERT INTO mentor_languages (mentor_id, language_id) VALUES (?, ?)";
  private static final String SQL_FOCUS_INSERT =
      "INSERT INTO mentor_mentorship_focus_areas (mentor_id, focus_area_id) VALUES (?, ?)";

  @Setter private static JdbcTemplate jdbc;

  private MentorSkillsMapper() {
    // Utility class - private constructor
  }

  /** Inserts the skills (technical areas and programming languages) for the mentor. */
  public static void insertSkills(final Mentor mentor, final Long memberId) {
    if (jdbc == null) {
      log.error("JdbcTemplate not set in MentorSkillsMapper");
      throw new IllegalStateException("JdbcTemplate not initialized");
    }
    insertTechnicalAreas(mentor.getSkills(), memberId);
    insertFocusArea(mentor.getSkills(), memberId);
    insertLanguages(mentor.getSkills(), memberId);
  }

  /* default */ private static void insertTechnicalAreas(
      final Skills mentorSkills, final Long memberId) {
    for (final TechnicalArea area : mentorSkills.areas()) {
      jdbc.update(SQL_TECH_AREAS_INSERT, memberId, area.getTechnicalAreaId());
    }
  }

  /* default */ private static void insertLanguages(
      final Skills mentorSkills, final Long memberId) {
    for (final Languages lang : mentorSkills.languages()) {
      jdbc.update(SQL_PROG_LANG_INSERT, memberId, lang.getLangId());
    }
  }

  /* default */ private static void insertFocusArea(
      final Skills mentorSkills, final Long memberId) {
    for (final MentorshipFocusArea focus : mentorSkills.mentorshipFocus()) {
      jdbc.update(SQL_FOCUS_INSERT, memberId, focus.getFocusId());
    }
  }
}
