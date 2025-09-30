package com.wcc.platform.repository.postgres.component;

import static com.wcc.platform.repository.postgres.constants.MentorConstants.*;
import static io.swagger.v3.core.util.Constants.COMMA;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.cms.pages.mentorship.Availability;
import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.repository.postgres.PostgresMemberRepository;
import com.wcc.platform.repository.postgres.PostgresMenteeSectionRepository;
import com.wcc.platform.repository.postgres.PostgresSkillRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Maps database rows to Mentor domain objects. */
@Component
@RequiredArgsConstructor
public class MentorMapper {
  private static final String SQL_INSERT_MENTOR =
      "INSERT INTO mentors (mentor_id, profile_status, bio, years_experience, "
          + " spoken_languages, is_available) VALUES (?, ?, ?, ?, ?, ?)";
  private static final String INSERT_MENTOR_MENTEE =
      "INSERT INTO mentor_mentee_section "
          + "(mentor_id, ideal_mentee, focus, additional) VALUES (?, ?, ?, ?)";
  private static final String INSERT_AVAILABILITY =
      "INSERT INTO mentor_availability " + "(mentor_id, month_num, hours) VALUES (?, ?, ?)";
  private static final String INSERT_MTRSHIP_TYPES =
      "INSERT INTO " + "mentor_mentorship_types (mentor_id, mentorship_type) VALUES (?, ?)";
  private static final String SQL_TECH_AREAS_INSERT =
      "INSERT INTO mentor_technical_areas " + "(mentor_id, technical_area_id) VALUES (?, ?)";
  private static final String SQL_PROG_LANG_INSERT =
      "INSERT INTO mentor_languages " + "(mentor_id, language_id) VALUES (?, ?)";

  private final JdbcTemplate jdbc;
  private final PostgresMemberRepository memberRepository;
  private final PostgresSkillRepository skillsRepository;
  private final PostgresMenteeSectionRepository menteeSectionRepo;

  /** Maps a ResultSet row to a Mentor object. */
  public Mentor mapRowToMentor(final ResultSet rs) throws SQLException {
    final long mentorId = rs.getLong(COLUMN_MENTOR_ID);

    final Optional<Member> memberOpt = memberRepository.findById(mentorId);
    final Member member = memberOpt.get();

    return Mentor.mentorBuilder()
        .id(mentorId)
        .fullName(member.getFullName())
        .position(member.getPosition())
        .email(member.getEmail())
        .slackDisplayName(member.getSlackDisplayName())
        .country(member.getCountry())
        .city(member.getCity())
        .companyName(member.getCompanyName())
        .images(member.getImages())
        .network(member.getNetwork())
        .profileStatus(ProfileStatus.fromId(rs.getInt(COLUMN_PROFILE_STATUS)))
        .skills(skillsRepository.findByMentorId(mentorId).get())
        .spokenLanguages(List.of(rs.getString(COLUMN_SPOKEN_LANG).split(COMMA)))
        .bio(rs.getString(COLUMN_BIO))
        .menteeSection(menteeSectionRepo.findByMentorId(mentorId).get())
        .build();
  }

  /** Inserts a new Mentor into the database, including related skills and mentee section. */
  public void addMentor(final Mentor mentor, final Long memberId) {
    insertMentor(mentor, memberId);
    insertMenteeSection(mentor.getMenteeSection(), memberId);
    insertSkills(mentor, memberId);
  }

  /** Inserts mentor-specific details into the mentors table. */
  private void insertMentor(final Mentor mentor, final Long memberId) {
    final var profileStatus = mentor.getProfileStatus();
    final var skills = mentor.getSkills();
    jdbc.update(
        SQL_INSERT_MENTOR,
        memberId,
        profileStatus.getStatusId(),
        mentor.getBio(),
        skills.yearsExperience(),
        String.join(",", mentor.getSpokenLanguages()),
        true);
  }

  /** Inserts the mentee section details for the mentor in mentor_mentee_section table. */
  private void insertMenteeSection(final MenteeSection menteeSec, final Long memberId) {
    jdbc.update(
        INSERT_MENTOR_MENTEE,
        memberId,
        menteeSec.idealMentee(),
        String.join(",", menteeSec.focus()),
        menteeSec.additional());
    insertAvailability(menteeSec, memberId);
    insertMentorshipTypes(menteeSec, memberId);
  }

  /** Inserts availability records for the mentor in mentor_availability table. */
  private void insertAvailability(final MenteeSection ms, final Long memberId) {
    for (final Availability a : ms.availability()) {
      jdbc.update(INSERT_AVAILABILITY, memberId, a.month().getValue(), a.hours());
    }
  }

  /** Inserts mentorship types for the mentor in mentor_mentorship_types table. */
  private void insertMentorshipTypes(final MenteeSection ms, final Long memberId) {
    for (final MentorshipType mt : ms.mentorshipType()) {
      jdbc.update(INSERT_MTRSHIP_TYPES, memberId, mt.getMentorshipTypeId());
    }
  }

  /** Inserts the skills (technical areas and programming languages) for the mentor. */
  private void insertSkills(final Mentor mentor, final Long memberId) {
    insertTechnicalAreas(mentor.getSkills(), memberId);
    insertLanguages(mentor.getSkills(), memberId);
  }

  /** Inserts technical areas for the mentor in mentor_technical_areas table. */
  private void insertTechnicalAreas(final Skills mentorSkills, final Long memberId) {
    for (final TechnicalArea area : mentorSkills.areas()) {
      jdbc.update(SQL_TECH_AREAS_INSERT, memberId, area.getTechnicalAreaId());
    }
  }

  /** Inserts programming languages for the mentor in mentor_languages table. */
  private void insertLanguages(final Skills mentorSkills, final Long memberId) {
    for (final Languages lang : mentorSkills.languages()) {
      jdbc.update(SQL_PROG_LANG_INSERT, memberId, lang.getLangId());
    }
  }
}
