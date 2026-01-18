package com.wcc.platform.repository.postgres.mentorship;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.exceptions.MenteeNotSavedException;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.postgres.component.MemberMapper;
import com.wcc.platform.repository.postgres.component.MenteeMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PostgresMenteeRepository implements MenteeRepository {
  private static final String SQL_GET_BY_ID = "SELECT * FROM mentees WHERE mentee_id = ?";
  private static final String SQL_DELETE_BY_ID = "DELETE FROM mentees WHERE mentee_id = ?";
  private static final String SELECT_ALL_MENTEES = "SELECT * FROM mentees";
  private static final String SQL_INSERT_MENTEE =
      "INSERT INTO mentees (mentee_id, mentees_profile_status, bio, years_experience, "
          + "spoken_languages) VALUES (?, ?, ?, ?, ?)";
  private static final String SQL_PROG_LANG_INSERT =
      "INSERT INTO mentee_languages (mentee_id, language_id) VALUES (?, ?)";
  private static final String SQL_TECH_AREAS_INSERT =
      "INSERT INTO mentee_technical_areas (mentee_id, technical_area_id) VALUES (?, ?)";
  private static final String INSERT_FOCUS_AREAS =
      "INSERT INTO mentee_mentorship_focus_areas (mentee_id, focus_area_id) VALUES (?, ?)";
  private static final String SQL_UPDATE_MENTEE =
      "UPDATE mentees SET mentees_profile_status = ?, bio = ?, years_experience = ?, "
          + "spoken_languages = ? WHERE mentee_id = ?";
  private static final String SQL_DELETE_TECH_AREAS =
      "DELETE FROM mentee_technical_areas WHERE mentee_id = ?";
  private static final String SQL_DELETE_LANGUAGES =
      "DELETE FROM mentee_languages WHERE mentee_id = ?";
  private static final String SQL_DELETE_FOCUS_AREAS =
      "DELETE FROM mentee_mentorship_focus_areas WHERE mentee_id = ?";

  private final JdbcTemplate jdbc;
  private final MenteeMapper menteeMapper;
  private final MemberMapper memberMapper;

  @Override
  @Transactional
  public Mentee create(final Mentee mentee) {
    final Long memberId = memberMapper.addMember(mentee);

    insertMenteeDetails(mentee, memberId);
    insertTechnicalAreas(mentee.getSkills(), memberId);
    insertLanguages(mentee.getSkills(), memberId);
    insertMentorshipFocusAreas(mentee.getSkills(), memberId);

    return findById(memberId)
        .orElseThrow(
            () -> new MenteeNotSavedException("Unable to save mentee " + mentee.getEmail()));
  }

  @Override
  @Transactional
  public Mentee update(final Long id, final Mentee mentee) {
    memberMapper.updateMember(mentee, id);

    updateMenteeDetails(mentee, id);

    jdbc.update(SQL_DELETE_TECH_AREAS, id);
    jdbc.update(SQL_DELETE_LANGUAGES, id);
    jdbc.update(SQL_DELETE_FOCUS_AREAS, id);

    insertTechnicalAreas(mentee.getSkills(), id);
    insertLanguages(mentee.getSkills(), id);
    insertMentorshipFocusAreas(mentee.getSkills(), id);

    return findById(id)
        .orElseThrow(() -> new MenteeNotSavedException("Unable to update mentee " + id));
  }

  @Override
  public Optional<Mentee> findById(final Long menteeId) {
    return jdbc.query(
        SQL_GET_BY_ID,
        rs -> {
          if (rs.next()) {
            return Optional.of(menteeMapper.mapRowToMentee(rs));
          }
          return Optional.empty();
        },
        menteeId);
  }

  @Override
  public List<Mentee> getAll() {
    return jdbc.query(SELECT_ALL_MENTEES, (rs, rowNum) -> menteeMapper.mapRowToMentee(rs));
  }

  @Override
  public void deleteById(final Long menteeId) {
    jdbc.update(SQL_DELETE_BY_ID, menteeId);
  }

  private void updateMenteeDetails(final Mentee mentee, final Long memberId) {
    final var profileStatus = mentee.getProfileStatus();
    final var skills = mentee.getSkills();
    jdbc.update(
        SQL_UPDATE_MENTEE,
        profileStatus.getStatusId(),
        mentee.getBio(),
        skills.yearsExperience(),
        String.join(",", mentee.getSpokenLanguages()),
        memberId);
  }

  private void insertMenteeDetails(final Mentee mentee, final Long memberId) {
    final var profileStatus = mentee.getProfileStatus();
    final var skills = mentee.getSkills();
    jdbc.update(
        SQL_INSERT_MENTEE,
        memberId,
        profileStatus.getStatusId(),
        mentee.getBio(),
        skills.yearsExperience(),
        String.join(",", mentee.getSpokenLanguages()));
  }

  /** Inserts technical areas for the mentee in mentee_technical_areas table. */
  private void insertTechnicalAreas(final Skills menteeSkills, final Long memberId) {
    for (final TechnicalArea area : menteeSkills.areas()) {
      jdbc.update(SQL_TECH_AREAS_INSERT, memberId, area.getTechnicalAreaId());
    }
  }

  /** Inserts programming languages for a mentee in mentee_languages table. */
  private void insertLanguages(final Skills menteeSkills, final Long memberId) {
    for (final Languages lang : menteeSkills.languages()) {
      jdbc.update(SQL_PROG_LANG_INSERT, memberId, lang.getLangId());
    }
  }

  /** Inserts focus areas for the mentorship for a mentee in mentee_mentorship_focus_areas table. */
  private void insertMentorshipFocusAreas(final Skills menteeSkills, final Long memberId) {
    for (final MentorshipFocusArea focus : menteeSkills.mentorshipFocus()) {
      jdbc.update(INSERT_FOCUS_AREAS, memberId, focus.getFocusId());
    }
  }
}
