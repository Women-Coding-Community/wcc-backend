package com.wcc.platform.repository.postgres;

import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.repository.MentorsRepository;
import com.wcc.platform.repository.postgres.component.MentorMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostgresMentorsRepository implements MentorsRepository {

  private static final String SQL_GET_BY_ID = "SELECT * FROM mentors WHERE id = ?";
  private static final String SQL_DELETE_BY_ID = "SELECT * FROM mentors WHERE id = ?";
  private static final String SQL_GET_BY_EMAIL =
      "SELECT mentors.* FROM mentors JOIN members ON mentors.id = members.id WHERE members.email = ?";
  private static final String SQL_GET_ALL = "SELECT * FROM mentors";
  private static final String SQL_FIND_ID_BY_EMAIL =
      "SELECT mentors.id FROM mentors JOIN members ON mentors.id = members.id WHERE members.email = ?";

  private final JdbcTemplate jdbc;
  private final MentorMapper mentorMapper;

  @Override
  public Optional<Mentor> findByEmail(String email) {
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
  public Long findIdByEmail(String email) {
    return jdbc.query(
        SQL_FIND_ID_BY_EMAIL,
        rs -> {
          if (rs.next()) {
            long id = rs.getLong("id");
            return Long.valueOf(id);
          }
          return null;
        },
        email);
  }

  @Override
  public Mentor create(Mentor entity) {
    return null;
  }

  @Override
  public Mentor update(Long id, Mentor entity) {
    return null;
  }

  @Override
  public Optional<Mentor> findById(Long id) {
    return jdbc.query(
        SQL_GET_BY_ID,
        rs -> {
          if (rs.next()) {
            return Optional.of(mentorMapper.mapRowToMentor(rs));
          }
          return Optional.empty();
        },
        id);
  }

  @Override
  public void deleteById(Long id) {
    jdbc.update(SQL_DELETE_BY_ID, id);
  }
}
