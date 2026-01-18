package com.wcc.platform.repository.postgres;

import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.repository.postgres.component.MemberMapper;
import com.wcc.platform.repository.postgres.component.MentorMapper;
import com.wcc.platform.repository.postgres.mentorship.PostgresMentorRepository;
import jakarta.validation.Validator;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

/** Test class for PostgresMentorRepository. */
class PostgresMentorRepositoryTest {
  private JdbcTemplate jdbc;
  private MemberMapper memberMapper;
  private MentorMapper mentorMapper;
  private PostgresMentorRepository repository;
  private Validator validator;

  @BeforeEach
  void setup() {
    jdbc = mock(JdbcTemplate.class);
    mentorMapper = mock(MentorMapper.class);
    memberMapper = mock(MemberMapper.class);
    validator = mock(Validator.class);
    when(validator.validate(any())).thenReturn(Collections.emptySet());
    repository = spy(new PostgresMentorRepository(jdbc, mentorMapper, memberMapper, validator));
  }

  @Test
  void testFindByEmailMentor() throws Exception {
    String email = "a@b.com";
    Mentor mapped = mock(Mentor.class);
    when(mentorMapper.mapRowToMentor(any(ResultSet.class))).thenReturn(mapped);
    when(jdbc.query(anyString(), any(ResultSetExtractor.class), eq(email)))
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
  void testFindIdByEmail() {
    String email = "x@y.com";
    long expectedId = 42L;

    when(jdbc.query(anyString(), any(ResultSetExtractor.class), eq(email)))
        .thenAnswer(
            invocation -> {
              ResultSetExtractor<?> extractor = invocation.getArgument(1);
              ResultSet resultSet = mock(ResultSet.class);
              when(resultSet.next()).thenReturn(true);
              when(resultSet.getLong("mentor_id")).thenReturn(expectedId);
              return extractor.extractData(resultSet);
            });

    Long mentorId = repository.findIdByEmail(email);

    assertEquals(expectedId, mentorId);
  }

  @Test
  void testCreate() {
    var mentor = createMentorTest();
    when(memberMapper.addMember(any())).thenReturn(1L);
    doNothing().when(mentorMapper).addMentor(any(), eq(1L));
    doReturn(Optional.of(mentor)).when(repository).findById(1L);

    Mentor result = repository.create(mentor);

    assertNotNull(result);
    assertEquals("Mentor bio", result.getBio());
  }

  @Test
  void testDeleteById() {
    long mentorId = 7L;
    repository.deleteById(mentorId);

    verify(jdbc).update("DELETE FROM mentors WHERE mentor_id = ?", mentorId);
  }

  @Test
  void testUpdateMentor() {
    Mentor updatedMentor = createMentorTest();

    doNothing().when(mentorMapper).updateMentor(any(), anyLong());
    doReturn(Optional.of(updatedMentor)).when(repository).findById(1L);

    Mentor result = repository.update(1L, updatedMentor);

    assertNotNull(result);
    assertEquals("Mentor bio", result.getBio());
  }

  @Test
  void testUpdateNonExistentMentor() {
    Mentor updatedMentor = createMentorTest();

    doNothing().when(mentorMapper).updateMentor(any(), eq(2L));
    doReturn(Optional.empty()).when(repository).findById(2L);

    try {
      repository.update(2L, updatedMentor);
    } catch (NoSuchElementException e) {
      assertNotNull(e);
    }
  }
}
