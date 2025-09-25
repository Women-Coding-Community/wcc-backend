package com.wcc.platform.repository.postgres.component;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.repository.postgres.PostgresMemberRepository;
import com.wcc.platform.repository.postgres.PostgresSkillsRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MentorMapper {

  private final JdbcTemplate jdbc;
  private final PostgresMemberRepository memberRepository;
  private final PostgresSkillsRepository skillsRepository;

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
        .profileStatus(ProfileStatus.fromId(rs.getInt("profile_status_id")))
        .skills(skillsRepository.findByMentorId(mentorId))
        .images(member.getImages())
        .city(member.getCity())
        .email(member.getEmail())
        .country(member.getCountry())
        .fullName(member.getFullName())
        .companyName(member.getCompanyName())
        .network(member.getNetwork())
        .position(member.getPosition())
        .slackDisplayName(member.getSlackDisplayName())
        .bio(rs.getString("bio"))
        .build();
  }

  List<String> getSpokenLanguages(long mentorId) {
    /*final String sql = "Select COALESCE(array_agg(DISTINCT ta.name) "
        + "FILTER (WHERE ta.name IS NOT NULL), ARRAY[]::text[]) AS spoken_languages FROM mentor_languages ml "
        + "JOIN spo ta ON ml.language_id = ta.id WHERE ml.mentor_id = ?";
    return jdbc.query(
        sql,
        rs -> {
          if (rs.next()) {
            return extractLanguages(rs);*/
    return null;
  }
}
