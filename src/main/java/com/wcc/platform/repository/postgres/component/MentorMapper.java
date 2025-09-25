package com.wcc.platform.repository.postgres.component;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.repository.postgres.PostgresMemberRepository;
import com.wcc.platform.repository.postgres.PostgresMenteeSectionRepository;
import com.wcc.platform.repository.postgres.PostgresSkillsRepository;
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
  private final PostgresSkillsRepository skillsRepository;
  private final PostgresMenteeSectionRepository menteeSectionRepository;

  public Mentor mapRowToMentor(final ResultSet rs) throws SQLException {
    final long mentorId = rs.getLong("mentor_id");

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
        .profileStatus(ProfileStatus.fromId(rs.getInt("profile_status_id")))
        .skills(skillsRepository.findByMentorId(mentorId).get())
        .spokenLanguages(List.of(rs.getString("spoken_language").split(",")))
        .bio(rs.getString("bio"))
        .menteeSection(menteeSectionRepository.findByMentorId(mentorId).get())
        .build();
  }
}
