package com.wcc.platform.repository.postgres.component;

import static com.wcc.platform.repository.postgres.component.MentorMentorshipMapper.insertMenteeSection;
import static com.wcc.platform.repository.postgres.component.MentorSkillsMapper.insertSkills;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.*;
import static io.swagger.v3.core.util.Constants.COMMA;

import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.cms.pages.mentorship.MentorMonthAvailability;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.Mentor.MentorBuilder;
import com.wcc.platform.repository.postgres.PostgresMemberRepository;
import com.wcc.platform.repository.postgres.PostgresMenteeSectionRepository;
import com.wcc.platform.repository.postgres.PostgresSkillRepository;
import jakarta.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Maps database rows to Mentor domain objects. */
@Component
@RequiredArgsConstructor
public class MentorMapper {
  private static final String SQL_INSERT_MENTOR =
      "INSERT INTO mentors (mentor_id, profile_status, bio, years_experience, "
          + " spoken_languages, is_available) VALUES (?, ?, ?, ?, ?, ?)";

  private final JdbcTemplate jdbc;
  private final PostgresMemberRepository memberRepository;
  private final PostgresSkillRepository skillsRepository;
  private final PostgresMenteeSectionRepository menteeSectionRepo;

  @PostConstruct
  public void initStaticMappers() {
    MentorMentorshipMapper.setJdbc(jdbc);
    MentorSkillsMapper.setJdbc(jdbc);
  }

  /** Maps a ResultSet row to a Mentor object. */
  public Mentor mapRowToMentor(final ResultSet rs) throws SQLException {
    final long mentorId = rs.getLong(COLUMN_MENTOR_ID);
    final MentorBuilder builder = Mentor.mentorBuilder();

    final Optional<Member> memberOpt = memberRepository.findById(mentorId);

    memberOpt.ifPresent(
        member ->
            builder
                .fullName(member.getFullName())
                .position(member.getPosition())
                .email(member.getEmail())
                .slackDisplayName(member.getSlackDisplayName())
                .country(member.getCountry())
                .city(member.getCity())
                .companyName(member.getCompanyName())
                .images(member.getImages())
                .network(member.getNetwork()));

    final var skillsMentor = skillsRepository.findSkills(mentorId);
    skillsMentor.ifPresent(builder::skills);

    final var menteeSection = menteeSectionRepo.findByMentorId(mentorId);
    menteeSection.ifPresent(builder::menteeSection);

    return builder
        .id(mentorId)
        .profileStatus(ProfileStatus.fromId(rs.getInt(COLUMN_PROFILE_STATUS)))
        .spokenLanguages(List.of(rs.getString(COLUMN_SPOKEN_LANG).split(COMMA)))
        .bio(rs.getString(COLUMN_BIO))
        .build();
  }

  /** Inserts a new Mentor into the database, including related skills and mentee section. */
  public void addMentor(final Mentor mentor, final Long memberId) {
    insertMentor(mentor, memberId);
    insertMenteeSection(mentor.getMenteeSection(), memberId);
    insertSkills(mentor, memberId);
  }

  /** Inserts mentor-specific details into the mentor. */
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

  /**
   * Updates an existing Mentor in the database, including related skills and mentee section. An
   * existing mentee section will be deleted and a new one inserted.
   */
  @Transactional
  public void updateMentor(final Mentor mentor, final Long mentorId) {
    updateMentorDetails(mentor, mentorId);
    updateMenteeSection(mentor.getMenteeSection(), mentorId);
    updateSkills(mentor, mentorId);
  }

  /** Updates mentor-specific details in the mentors table. */
  private void updateMentorDetails(final Mentor mentor, final Long mentorId) {
    final String sql =
        "UPDATE mentors SET "
            + "profile_status = ?, "
            + "bio = ?, "
            + "years_experience = ?, "
            + "spoken_languages = ?, "
            + "is_available = ? "
            + "WHERE mentor_id = ?";

    final var skills = mentor.getSkills();
    jdbc.update(
        sql,
        mentor.getProfileStatus().getStatusId(),
        mentor.getBio(),
        skills.yearsExperience(),
        String.join(",", mentor.getSpokenLanguages()),
        true,
        mentorId);
  }

  /**
   * Updates the mentee section for a mentor (updates menteeSection, deletes old records of mentor
   * availability and type and inserts new ones).
   */
  private void updateMenteeSection(final MenteeSection menteeSec, final Long mentorId) {
    final String sql =
        "UPDATE mentor_mentee_section SET "
            + "ideal_mentee = ?, "
            + "additional = ? "
            + "WHERE mentor_id = ?";

    jdbc.update(sql, menteeSec.idealMentee(), menteeSec.additional(), mentorId);
    jdbc.update(
        "UPDATE mentor_mentorship_types SET mentorship_type = ? WHERE mentor_id = ?",
        menteeSec.mentorshipType(),
        mentorId);
    updateAvailability(menteeSec, mentorId);
  }

  private void updateAvailability(final MenteeSection ms, final Long memberId) {
    final String sql =
        "UPDATE mentor_availability SET "
            + "month_num = ?, "
            + "hours = ? "
            + "WHERE mentor_id = ?";

    for (final MentorMonthAvailability a : ms.availability()) {
      jdbc.update(sql, memberId, a.month().getValue(), a.hours());
    }
  }

  /** Updates skills for a mentor (deletes old records and inserts new ones). */
  private void updateSkills(final Mentor mentor, final Long mentorId) {
    jdbc.update("DELETE FROM mentor_technical_areas WHERE mentor_id = ?", mentorId);
    jdbc.update("DELETE FROM mentor_languages WHERE mentor_id = ?", mentorId);
    jdbc.update("DELETE FROM mentor_mentorship_focus_areas WHERE mentor_id = ?", mentorId);

    insertSkills(mentor, mentorId);
  }
}
