package com.wcc.platform.repository.postgres.component;

import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_BIO;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_MENTOR_ID;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_PROFILE_STATUS;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_SPOKEN_LANG;
import static io.swagger.v3.core.util.Constants.COMMA;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.repository.postgres.PostgresMemberRepository;
import com.wcc.platform.repository.postgres.PostgresMenteeSectionRepository;
import com.wcc.platform.repository.postgres.PostgresSkillRepository;
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
  private final PostgresMenteeSectionRepository menteeSectionRepository;

  public Mentor mapRowToMentor(final ResultSet rs) throws SQLException {
    final long mentorId = rs.getLong(COLUMN_MENTOR_ID);

    // Prefer to reuse existing Member mapping
    Optional<Member> memberOpt;
    try {
      memberOpt = memberRepository.findById(mentorId);
    } catch (Exception ex) {
      memberOpt = Optional.empty();
    }

    Member member = memberOpt.get();

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
        .menteeSection(menteeSectionRepository.findByMentorId(mentorId).get())
        .build();
  }
}
