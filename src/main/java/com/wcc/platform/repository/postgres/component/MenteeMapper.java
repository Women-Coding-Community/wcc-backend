package com.wcc.platform.repository.postgres.component;

import static com.wcc.platform.repository.postgres.constants.MentorConstants.COL_MENTORSHIP_TYPE;
import static io.swagger.v3.core.util.Constants.COMMA;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.Mentee.MenteeBuilder;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.mentorship.Skills;
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
        "INSERT INTO mentees (mentee_id, mentees_profile_status, bio, years_experience, "
            + "spoken_languages) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_PROG_LANG_INSERT =
        "INSERT INTO mentee_languages (mentee_id, language_id) VALUES (?, ?)";
    private static final String INSERT_MENTORSHIP_TYPES =
        "INSERT INTO mentee_mentorship_types (mentee_id, mentorship_type) VALUES (?, ?)";
    private static final String INSERT_PREVIOUS_MENTORSHIP_TYPES =
        "INSERT INTO mentee_previous_mentorship_types (mentee_id, mentorship_type) VALUES (?, ?)";
    private static final String SQL_TECH_AREAS_INSERT =
        "INSERT INTO mentee_technical_areas (mentee_id, technical_area_id) VALUES (?, ?)";
    private static final String INSERT_MENTORSHIP_FOCUS_AREAS =
        "INSERT INTO mentee_mentorship_focus_areas (mentee_id, focus_area_id) VALUES (?, ?)";
    private static final String SQL_MENTORSHIP_TYPE =
        "SELECT mentorship_type FROM mentee_mentorship_types WHERE mentee_id = ?";

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

        final var mentorshipType = loadMentorshipTypes(menteeId);
        mentorshipType.ifPresent(builder::mentorshipType);


        return builder
            .id(menteeId)
            .profileStatus(ProfileStatus.fromId(rs.getInt("mentees_profile_status")))
            .spokenLanguages(List.of(rs.getString("spoken_languages").split(COMMA)))
            .bio(rs.getString("bio"))
            .build();
    }

    public Optional<MentorshipType> loadMentorshipTypes(final Long menteeId) {
        List<MentorshipType> types = jdbc.query(
            SQL_MENTORSHIP_TYPE,
            (rs, rowNum) -> MentorshipType.fromId(rs.getInt(COL_MENTORSHIP_TYPE)),
            menteeId
        );

        if (types.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(types.get(0));
    }

    public void addMentee(final Mentee mentee, final Long memberId) {
        insertMentee(mentee, memberId);
        insertTechnicalAreas(mentee.getSkills(), memberId);
        insertLanguages(mentee.getSkills(), memberId);
        insertMentorshipTypes(mentee.getMentorshipType(), memberId);
        insertPreviousMentorshipTypes(mentee.getPreviousMentorshipType(), memberId);
        insertMentorshipFocusAreas(mentee.getSkills(), memberId);
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

    /** Inserts mentorship types for a mentee in mentee_mentorship_types table. */
    private void insertMentorshipTypes(final MentorshipType mt, final Long memberId) {
        jdbc.update(INSERT_MENTORSHIP_TYPES, memberId, mt.getMentorshipTypeId());
    }

    /** Inserts previous mentorship types for a mentee in mentee_previous_mentorship_types table. */
    private void insertPreviousMentorshipTypes(final MentorshipType mt, final Long memberId) {
        jdbc.update(INSERT_PREVIOUS_MENTORSHIP_TYPES, memberId, mt.getMentorshipTypeId());
    }

    /** Inserts focus areas for the mentorship for a mentee in mentee_mentorship_focus_areas table. */
    private void insertMentorshipFocusAreas(final Skills menteeSkills, final Long memberId) {
        for (final MentorshipFocusArea focus : menteeSkills.mentorshipFocus()) {
            jdbc.update(INSERT_MENTORSHIP_FOCUS_AREAS, memberId, focus.getFocusId());
        }
    }

}
