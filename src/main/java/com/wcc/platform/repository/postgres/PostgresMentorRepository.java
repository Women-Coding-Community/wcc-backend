package com.wcc.platform.repository.postgres;

import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_MENTOR_ID;

import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.postgres.component.MemberMapper;
import com.wcc.platform.repository.postgres.component.MentorMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PostgresMentorRepository implements MentorRepository {

  private static final String SQL_GET_BY_ID = "SELECT * FROM mentors WHERE mentor_id = ?";
  private static final String SQL_DELETE_BY_ID = "SELECT * FROM mentors WHERE mentor_id = ?";
  private static final String SQL_GET_BY_EMAIL =
      "SELECT mentors.* FROM mentors LEFT JOIN members ON mentors.mentor_id = members.id "
          + "WHERE members.email = ?";
  private static final String SQL_GET_ALL = "SELECT * FROM mentors";
  private static final String SQL_FIND_ID_BY_EMAIL =
      "SELECT mentors.mentor_id FROM mentors LEFT JOIN members ON mentors.mentor_id = members.id"
          + " WHERE members.email = ?";

  private final JdbcTemplate jdbc;
  private final MentorMapper mentorMapper;
  private final MemberMapper memberMapper;

  @Override
  public Optional<Mentor> findByEmail(final String email) {
    return jdbc.query(
        SQL_GET_BY_EMAIL,
        rs -> {
          if (rs.next()) {
            return Optional.of(mentorMapper.mapRowToMentor(rs));
          }
          return Optional.empty();
        },
        email);
  }

  @Override
  public List<Mentor> getAll() {
    return jdbc.query(SQL_GET_ALL, (rs, rowNum) -> mentorMapper.mapRowToMentor(rs));
  }

  @Override
  public Long findIdByEmail(final String email) {
    return jdbc.query(
        SQL_FIND_ID_BY_EMAIL,
        rs -> {
          if (rs.next()) {
            return rs.getLong(COLUMN_MENTOR_ID);
          }
          return null;
        },
        email);
  }

  @Override
  @Transactional
  public Mentor create(final Mentor mentor) {
    final Long memberId = memberMapper.addMember(mentor);
    mentorMapper.addMentor(mentor, memberId);
    final var mentorAdded = findById(memberId);
    return mentorAdded.orElse(null);
  }

  @Override
  public Mentor update(final Long mentorId, final Mentor mentor) {
    return null;
  }

  @Override
  public Optional<Mentor> findById(final Long mentorId) {
    return jdbc.query(
        SQL_GET_BY_ID,
        rs -> {
          if (rs.next()) {
            return Optional.of(mentorMapper.mapRowToMentor(rs));
          }
          return Optional.empty();
        },
        mentorId);
  }

  @Override
  public void deleteById(final Long mentorId) {
    jdbc.update(SQL_DELETE_BY_ID, mentorId);
  }
}
