package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.cms.attributes.Experience;
import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.repository.SkillsRepository;
import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class PostgresSkillsRepository implements SkillsRepository {

  private static final String SELECT_MENTOR_SKILLS =
      "SELECT m.years_experience, "
          + "  COALESCE(array_agg(DISTINCT ta.name) FILTER (WHERE ta.name IS NOT NULL), ARRAY[]::text[]) AS technical_areas, "
          + "  COALESCE(array_agg(DISTINCT l.name) FILTER (WHERE l.name IS NOT NULL), ARRAY[]::text[]) AS languages "
          + "FROM mentors m "
          + "LEFT JOIN mentor_technical_areas mta ON m.mentor_id = mta.mentor_id "
          + "LEFT JOIN technical_areas ta ON mta.technical_area_id = ta.id "
          + "LEFT JOIN mentor_languages ml ON m.mentor_id = ml.mentor_id "
          + "LEFT JOIN languages l ON ml.language_id = l.id "
          + "WHERE m.mentor_id = ? "
          + "GROUP BY m.mentor_id, m.years_experience";

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Skills findByMentorId(Long mentorId) {
    try {
      return jdbcTemplate.queryForObject(
          SELECT_MENTOR_SKILLS,
          (rs, rowNum) -> {
            Integer years =
                rs.getObject("years_experience") == null ? null : rs.getInt("years_experience");

            List<TechnicalArea> areas = extractTechnicalAreas(rs);
            List<Languages> languages = extractLanguages(rs);

            return new Skills(years, Experience.fromYears(years), areas, languages);
          },
          mentorId);
    } catch (EmptyResultDataAccessException ex) {
      return new Skills(null, null, Collections.emptyList(), Collections.emptyList());
    }
  }

  private List<TechnicalArea> extractTechnicalAreas(ResultSet rs) {
    try {
      Array arr = rs.getArray("technical_areas");
      if (arr == null) return Collections.emptyList();
      Object[] values = (Object[]) arr.getArray();
      List<TechnicalArea> out = new ArrayList<>(values.length);
      for (Object v : values) {
        if (v == null) continue;
        try {
          out.add(TechnicalArea.valueOf(v.toString()));
        } catch (IllegalArgumentException ignored) {
          // unknown value — skip
        }
      }
      return out;
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  private List<Languages> extractLanguages(ResultSet rs) {
    try {
      Array arr = rs.getArray("languages");
      if (arr == null) return Collections.emptyList();
      Object[] values = (Object[]) arr.getArray();
      List<Languages> out = new ArrayList<>(values.length);
      for (Object v : values) {
        if (v == null) continue;
        try {
          out.add(Languages.valueOf(v.toString()));
        } catch (IllegalArgumentException ignored) {
          // unknown value — skip
        }
      }
      return out;
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }
}
