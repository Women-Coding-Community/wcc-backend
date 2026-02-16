package com.wcc.platform.repository.postgres.component;

import static com.wcc.platform.repository.postgres.constants.MentorConstants.*;
import static io.swagger.v3.core.util.Constants.COMMA;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.Mentor.MentorBuilder;
import com.wcc.platform.repository.postgres.PostgresMemberRepository;
import com.wcc.platform.repository.postgres.mentorship.PostgresMenteeSectionRepository;
import com.wcc.platform.repository.postgres.mentorship.PostgresSkillRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Maps database rows to Mentor domain objects. */
@Component
@RequiredArgsConstructor
public class MentorMapper {

  private final PostgresMemberRepository memberRepository;
  private final PostgresSkillRepository skillsRepository;
  private final PostgresMenteeSectionRepository menteeSectionRepo;

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
                .pronouns(member.getPronouns())
                .pronounCategory(member.getPronounCategory())
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
        .isWomenNonBinary(rs.getBoolean(COL_WOMEN_NON_BINARY))
        .acceptMale(rs.getBoolean(COL_ACCEPT_MALE))
        .acceptPromotion(rs.getBoolean(COL_ACCEPT_PROMO))
        .build();
  }

  public void addMentor(final Mentor mentor, final Long memberId) {
    menteeSectionRepo.insertMenteeSection(mentor.getMenteeSection(), memberId);
    skillsRepository.insertSkills(mentor, memberId);
  }

  public void updateMentor(final Mentor mentor, final Long mentorId) {
    menteeSectionRepo.updateMenteeSection(mentor.getMenteeSection(), mentorId);
    skillsRepository.updateSkills(mentor, mentorId);
  }
}
