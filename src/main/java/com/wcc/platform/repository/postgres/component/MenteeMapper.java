package com.wcc.platform.repository.postgres.component;

import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_BIO;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_MENTOR_ID;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_PROFILE_STATUS;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_SPOKEN_LANG;
import static io.swagger.v3.core.util.Constants.COMMA;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.Mentee.MenteeBuilder;
import com.wcc.platform.repository.SkillRepository;
import com.wcc.platform.repository.postgres.PostgresMemberRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenteeMapper {


    private static final String SQL_INSERT_MENTEE =
        "INSERT INTO mentees (mentee_id, profile_status, bio, years_experience, "
            + " spoken_languages) VALUES (?, ?, ?, ?, ?)";

    private final JdbcTemplate jdbc;
    private final PostgresMemberRepository memberRepository;
    private final SkillRepository skillsRepository;

    /** Maps a ResultSet row to a Mentee object. */
    public Mentee mapRowToMentee(final ResultSet rs) throws SQLException {
        final long menteeId = rs.getLong("mentee_id");
        final MenteeBuilder builder = Mentee.menteeBuilder();

        final Optional<Member> memberOpt = memberRepository.findById(menteeId);

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

        final var skillsMentee = skillsRepository.findSkills(menteeId);
        skillsMentee.ifPresent(builder::skills);


        return builder
            .id(menteeId)
            .profileStatus(ProfileStatus.fromId(rs.getInt("profile_status")))
            .spokenLanguages(List.of(rs.getString("spoken_languages").split(COMMA)))
            .bio(rs.getString("bio"))
            .build();
    }

    public void addMentee(final Mentee mentee, final Long memberId) {
        insertMentee(mentee, memberId);
    }

    private void insertMentee(final Mentee mentee, final Long memberId) {
        final var profileStatus = mentee.getProfileStatus();
        final var skills = mentee.getSkills();
        jdbc.update(
            SQL_INSERT_MENTEE,
            memberId,
            profileStatus.getStatusId(),
            mentee.getBio(),
            skills.yearsExperience(),
            String.join(",", mentee.getSpokenLanguages())
        );
    }
}
