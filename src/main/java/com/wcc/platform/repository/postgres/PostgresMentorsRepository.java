package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.cms.attributes.Experience;
import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.cms.pages.mentorship.Availability;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.repository.MentorsRepository;
import com.wcc.platform.repository.postgres.component.MemberMapper;
import com.wcc.platform.repository.postgres.component.MentorMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PostgresMentorsRepository implements MentorsRepository {

  private static final String SQL_GET_BY_ID = "SELECT * FROM mentors WHERE mentor_id = ?";
  private static final String SQL_DELETE_BY_ID = "SELECT * FROM mentors WHERE mentor_id = ?";
  private static final String SQL_GET_BY_EMAIL =
      "SELECT mentors.* FROM mentors JOIN members ON mentors.mentor_id = members.id WHERE members.email = ?";
  private static final String SQL_GET_ALL = "SELECT * FROM mentors";
  private static final String SQL_INSERT =
      "INSERT INTO mentors (mentor_id, profile_status, bio, years_experience, spoken_languages, is_available) "
          + "VALUES (?, ?, ?, ?, ?, ?)";
  private static final String SQL_FIND_ID_BY_EMAIL =
      "SELECT mentors.mentor_id FROM mentors JOIN members ON mentors.mentor_id = members.id WHERE members.email = ?";

  private static final String MENTOR_ID_COLUMN = "mentor_id";
  private final JdbcTemplate jdbc;
  private final MentorMapper mentorMapper;
  private final PostgresCountryRepository countryRepository;
  private final MemberMapper memberMapper;

  @Override
  public Optional<Mentor> findByEmail(String email) {
    return jdbc.query(
        SQL_GET_BY_EMAIL,
        rs -> {
          if (rs.next()) {
            return Optional.of(mentorMapper.mapRowToMentor(rs));
          }
          return Optional.empty();
        },
        email);
  }

  @Override
  public List<Mentor> getAll() {
    return jdbc.query(SQL_GET_ALL, (rs, rowNum) -> mentorMapper.mapRowToMentor(rs));
  }

  @Override
  public Long findIdByEmail(String email) {
    return jdbc.query(
        SQL_FIND_ID_BY_EMAIL,
        rs -> {
          if (rs.next()) {
            long id = rs.getLong(MENTOR_ID_COLUMN);
            return Long.valueOf(id);
          }
          return null;
        },
        email);
  }

  @Override
  @Transactional
  public Mentor create(Mentor entity) {
    if (entity == null) {
      throw new IllegalArgumentException("Mentor entity must not be null");
    }

    String SQL_INSERT_MEMBER =
        "INSERT INTO members (full_name, slack_name, position, company_name, email, city, "
            + "country_id, status_id, bio, years_experience, spoken_language) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, NULL) RETURNING id";
    final Long memberId = memberMapper.addMember(entity, SQL_INSERT_MEMBER);

    Skills mentorSkills = entity.getSkills();

    // 1. Insert into mentors
    jdbc.update(
        "INSERT INTO mentors (mentor_id, profile_status, bio, years_experience, experience_range, spoken_languages, is_available) VALUES (?, ?, ?, ?, ?, ?, ?)",
        memberId,
        entity.getProfileStatus().getStatus_id(),
        entity.getBio(),
        mentorSkills.yearsExperience(),
        Experience.fromYears(mentorSkills.yearsExperience()).getExperienceRange(),
        String.join(",", entity.getSpokenLanguages()),
        true);

    // 2. Insert into mentor_mentee_section
    MenteeSection ms = entity.getMenteeSection();
    jdbc.update(
        "INSERT INTO mentor_mentee_section (mentor_id, ideal_mentee, focus, additional) VALUES (?, ?, ?, ?)",
        memberId,
        ms.idealMentee(),
        String.join(",", ms.focus()),
        ms.additional());

    // 3. Insert into mentor_availability
    for (Availability a : ms.availability()) {
      jdbc.update(
          "INSERT INTO mentor_availability (mentor_id, month, hours) VALUES (?, ?, ?)",
          memberId,
          a.month().name(),
          a.hours());
    }
    // 4. Insert into mentor_mentorship_types
    for (MentorshipType mt : ms.mentorshipType()) {
      jdbc.update(
          "INSERT INTO mentor_mentorship_types (mentor_id, mentorship_type) VALUES (?, ?)",
          memberId,
          mt.getMentorshipTypeId());
    }

    // 2. Insert into mentor_technical_areas
    for (TechnicalArea area : mentorSkills.areas()) {
      jdbc.update(
          "INSERT INTO mentor_technical_areas (mentor_id, technical_area_id) VALUES (?, ?)",
          memberId,
          area.getTechnicalAreaId());
    }

    // 3. Insert into mentor_languages
    for (Languages lang : mentorSkills.languages()) {
      jdbc.update(
          "INSERT INTO mentor_languages (mentor_id, language_id) VALUES (?, ?)",
          memberId,
          lang.getId());
    }

    return findById(memberId).isPresent() ? findById(memberId).get() : null;
  }

  @Override
  public Mentor update(Long id, Mentor entity) {
    return null;
  }

  @Override
  public Optional<Mentor> findById(Long id) {
    return jdbc.query(
        SQL_GET_BY_ID,
        rs -> {
          if (rs.next()) {
            return Optional.of(mentorMapper.mapRowToMentor(rs));
          }
          return Optional.empty();
        },
        id);
  }

  @Override
  public void deleteById(Long id) {
    jdbc.update(SQL_DELETE_BY_ID, id);
  }
}
