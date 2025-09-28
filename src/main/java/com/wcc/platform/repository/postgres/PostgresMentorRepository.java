package com.wcc.platform.repository.postgres;

import static com.wcc.platform.domain.platform.constants.MentorConstants.COLUMN_MENTOR_ID;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.cms.pages.mentorship.Availability;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.repository.MentorRepository;
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
public class PostgresMentorRepository implements MentorRepository {

  private static final String SQL_GET_BY_ID = "SELECT * FROM mentors WHERE mentor_id = ?";
  private static final String SQL_DELETE_BY_ID = "SELECT * FROM mentors WHERE mentor_id = ?";
  private static final String SQL_GET_BY_EMAIL =
      "SELECT mentors.* FROM mentors LEFT JOIN members ON mentors.mentor_id = members.id "
          + "WHERE members.email = ?";
  private static final String SQL_GET_ALL = "SELECT * FROM mentors";
  private static final String SQL_INSERT_MENTOR =
      "INSERT INTO mentors (mentor_id, profile_status, bio, years_experience, "
          + " spoken_languages, is_available) VALUES (?, ?, ?, ?, ?, ?)";
  private static final String SQL_FIND_ID_BY_EMAIL =
      "SELECT mentors.mentor_id FROM mentors LEFT JOIN members ON mentors.mentor_id = members.id"
          + " WHERE members.email = ?";
  private static final String SQL_INSERT_MEMBER =
      "INSERT INTO members (full_name, slack_name, position, company_name, email, city, "
          + "country_id, status_id, bio, years_experience, spoken_language) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL, NULL) RETURNING id";
  private static final String SQL_MENTOR_MENTEE_INSERT =
      "INSERT INTO mentor_mentee_section "
          + "(mentor_id, ideal_mentee, focus, additional) VALUES (?, ?, ?, ?)";
  private static final String SQL_AVAILABILITY_INSERT =
      "INSERT INTO mentor_availability " + "(mentor_id, month, hours) VALUES (?, ?, ?)";
  private static final String SQL_MENTORSHIP_TYPES_INSERT =
      "INSERT INTO " + "mentor_mentorship_types (mentor_id, mentorship_type) VALUES (?, ?)";
  private static final String SQL_TECH_AREAS_INSERT =
      "INSERT INTO mentor_technical_areas " + "(mentor_id, technical_area_id) VALUES (?, ?)";
  private static final String SQL_PROG_LANG_INSERT =
      "INSERT INTO mentor_languages " + "(mentor_id, language_id) VALUES (?, ?)";

  private final JdbcTemplate jdbc;
  private final MentorMapper mentorMapper;
  private final PostgresCountryRepository countryRepository;
  private final MemberMapper memberMapper;

  @Override
  public Optional<Mentor> findByEmail(final String email) {
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
  public Long findIdByEmail(final String email) {
    return jdbc.query(
        SQL_FIND_ID_BY_EMAIL,
        rs -> {
          if (rs.next()) {
            long id = rs.getLong(COLUMN_MENTOR_ID);
            return Long.valueOf(id);
          }
          return null;
        },
        email);
  }

  @Override
  @Transactional
  public Mentor create(final Mentor mentor) {
    if (mentor == null) {
      throw new IllegalArgumentException("Mentor mentor must not be null");
    }

    final Long memberId = memberMapper.addMember(mentor, SQL_INSERT_MEMBER);

    Skills mentorSkills = mentor.getSkills();

    // 1. Insert into mentors
    jdbc.update(
        SQL_INSERT_MENTOR,
        memberId,
        mentor.getProfileStatus().getStatusId(),
        mentor.getBio(),
        mentorSkills.yearsExperience(),
        String.join(",", mentor.getSpokenLanguages()),
        true);

    // 2. Insert into mentor_mentee_section
    MenteeSection ms = mentor.getMenteeSection();
    jdbc.update(
        SQL_MENTOR_MENTEE_INSERT,
        memberId,
        ms.idealMentee(),
        String.join(",", ms.focus()),
        ms.additional());

    // 3. Insert into mentor_availability
    for (Availability a : ms.availability()) {
      jdbc.update(SQL_AVAILABILITY_INSERT, memberId, a.month().name(), a.hours());
    }
    // 4. Insert into mentor_mentorship_types
    for (MentorshipType mt : ms.mentorshipType()) {
      jdbc.update(SQL_MENTORSHIP_TYPES_INSERT, memberId, mt.getMentorshipTypeId());
    }

    // 2. Insert into mentor_technical_areas
    for (TechnicalArea area : mentorSkills.areas()) {
      jdbc.update(SQL_TECH_AREAS_INSERT, memberId, area.getTechnicalAreaId());
    }

    // 3. Insert into mentor_languages
    for (Languages lang : mentorSkills.languages()) {
      jdbc.update(SQL_PROG_LANG_INSERT, memberId, lang.getId());
    }

    var mentorAdded = findById(memberId);
    return mentorAdded.orElse(null);
  }

  @Override
  public Mentor update(final Long mentorId, final Mentor mentor) {
    return null;
  }

  @Override
  public Optional<Mentor> findById(final Long mentorId) {
    return jdbc.query(
        SQL_GET_BY_ID,
        rs -> {
          if (rs.next()) {
            return Optional.of(mentorMapper.mapRowToMentor(rs));
          }
          return Optional.empty();
        },
        mentorId);
  }

  @Override
  public void deleteById(final Long mentorId) {
    jdbc.update(SQL_DELETE_BY_ID, mentorId);
  }
}
