package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.postgres.component.MemberMapper;
import com.wcc.platform.repository.postgres.component.MenteeMapper;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PostgresMenteeRepository implements MenteeRepository {
    private static final String SQL_GET_BY_ID = "SELECT * FROM mentees WHERE mentee_id = ?";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM mentees WHERE mentee_id = ?";
    private static final String SELECT_ALL_MENTEES = "SELECT * FROM mentees";


    private final JdbcTemplate jdbc;
    private final MenteeMapper menteeMapper;
    private final MemberMapper memberMapper;

    @Override
    @Transactional
    public Mentee create(final Mentee mentee) {
        final Long memberId = memberMapper.addMember(mentee);
        // TODO: cycleYear should be passed from service layer, using current year as temporary solution
        final Integer cycleYear = Year.now().getValue();
        menteeMapper.addMentee(mentee, memberId, cycleYear);
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
        return jdbc.query(
            SQL_GET_BY_ID,
            rs -> {
                if (rs.next()) {
                    return Optional.of(menteeMapper.mapRowToMentee(rs));
                }
                return Optional.empty();
            },
            menteeId);
    }

    @Override
    public List<Mentee> getAll() {
        return jdbc.query(SELECT_ALL_MENTEES, (rs, rowNum) -> menteeMapper.mapRowToMentee(rs));
    }

    @Override
    public void deleteById(final Long menteeId) {
        jdbc.update(SQL_DELETE_BY_ID, menteeId);
    }
}
