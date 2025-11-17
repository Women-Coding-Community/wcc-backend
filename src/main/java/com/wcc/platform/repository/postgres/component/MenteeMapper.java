package com.wcc.platform.repository.postgres.component;

import com.wcc.platform.domain.platform.mentorship.Mentee;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenteeMapper {

    private static final String SQL_INSERT_MENTEE =
        "INSERT INTO mentees (mentee_id, profile_status, bio, years_experience, "
            + " spoken_languages, is_available) VALUES (?, ?, ?, ?, ?, ?)";

    private final JdbcTemplate jdbc;

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
            String.join(",", mentee.getSpokenLanguages()),
            true
        );
    }
}
