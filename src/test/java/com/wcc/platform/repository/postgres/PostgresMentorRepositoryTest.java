package com.wcc.platform.repository.postgres;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.repository.postgres.component.MemberMapper;
import com.wcc.platform.repository.postgres.component.MentorMapper;
import java.sql.ResultSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

/** Test class for PostgresMentorRepository. */
public class PostgresMentorRepositoryTest {
  private JdbcTemplate jdbc;
  private MentorMapper mentorMapper;
  private PostgresMentorRepository repository;

  @BeforeEach
  void setup() {
    jdbc = mock(JdbcTemplate.class);
    mentorMapper = mock(MentorMapper.class);
    MemberMapper memberMapper;
    memberMapper = mock(MemberMapper.class);
    repository = spy(new PostgresMentorRepository(jdbc, mentorMapper, memberMapper));
  }

  @Test
  void testFindByEmailWhenOptionalMentor() throws Exception {
    // Arrange
    String email = "a@b.com";
    Mentor mapped = mock(Mentor.class);
    when(mentorMapper.mapRowToMentor(any(ResultSet.class))).thenReturn(mapped);
    when(jdbc.query(
            eq(
                "SELECT mentors.* FROM mentors "
                    + "LEFT JOIN members ON mentors.mentor_id = members.id "
                    + "WHERE members.email = ?"),
            any(ResultSetExtractor.class),
            eq(email)))
        .thenAnswer(
            invocation -> {
              ResultSetExtractor<?> extractor = invocation.getArgument(1);
              ResultSet resultSet = mock(ResultSet.class);
              when(resultSet.next()).thenReturn(true);
              return extractor.extractData(resultSet);
            });

    Optional<Mentor> opt = repository.findByEmail(email);

    assert opt.isPresent() && opt.get() == mapped;
    verify(mentorMapper).mapRowToMentor(any(ResultSet.class));
  }

  @Test
  void testFindIdByEmail() throws Exception {
    // Arrange
    String email = "x@y.com";
    long expectedId = 42L;

    when(jdbc.query(
            eq(
                "SELECT mentors.mentor_id FROM mentors "
                    + "LEFT JOIN members ON mentors.mentor_id = members.id"
                    + " WHERE members.email = ?"),
            any(ResultSetExtractor.class),
            eq(email)))
        .thenAnswer(
            invocation -> {
              ResultSetExtractor<?> extractor = invocation.getArgument(1);
              ResultSet resultSet = mock(ResultSet.class);
              when(resultSet.next()).thenReturn(true);
              when(resultSet.getLong("mentor_id")).thenReturn(expectedId);
              return extractor.extractData(resultSet);
            });

    Long mentorId = repository.findIdByEmail(email);

    assert mentorId != null && mentorId == expectedId;
  }

  @Test
  void testDeleteById() {
    long mentorId = 7L;
    repository.deleteById(mentorId);

    verify(jdbc).update(eq("SELECT * FROM mentors WHERE mentor_id = ?"), eq(mentorId));
  }
}
