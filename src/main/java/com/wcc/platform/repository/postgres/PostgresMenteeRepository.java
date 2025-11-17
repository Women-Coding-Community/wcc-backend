package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.postgres.component.MemberMapper;
import com.wcc.platform.repository.postgres.component.MenteeMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PostgresMenteeRepository implements MenteeRepository {


    private static final String SQL_DELETE_BY_ID = "DELETE FROM mentees WHERE mentee_id = ?";


    private final JdbcTemplate jdbc;
    private final MenteeMapper menteeMapper;
    private final MemberMapper memberMapper;

    @Override
    @Transactional
    public Mentee create(final Mentee mentee) {
        final Long memberId = memberMapper.addMember(mentee);
        menteeMapper.addMentee(mentee, memberId);
        final var menteeAdded = findById(memberId);
        return menteeAdded.orElse(null);
    }

    @Override
    public Mentee update(final Long id, final Mentee mentee) {
        //not implemented
        return mentee;
    }

    @Override
    public Optional<Mentee> findById(final Long menteeId) {
        //not implemented
        return null;
    }

    @Override
    public void deleteById(final Long menteeId) {
        jdbc.update(SQL_DELETE_BY_ID, menteeId);
    }
}
