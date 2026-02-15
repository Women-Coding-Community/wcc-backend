package com.wcc.platform.repository.postgres;

import static com.wcc.platform.factories.SetupMenteeFactories.createMenteeTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.platform.mentorship.LanguageProficiency;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.TechnicalAreaProficiency;
import com.wcc.platform.repository.postgres.component.MemberMapper;
import com.wcc.platform.repository.postgres.component.MenteeMapper;
import com.wcc.platform.repository.postgres.mentorship.PostgresMenteeRepository;
import jakarta.validation.Validator;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

class PostgresMenteeRepositoryTest {
  private static final String SQL_GET_BY_ID = "SELECT * FROM mentees WHERE mentee_id = ?";
  private static final String SELECT_ALL_MENTEES = "SELECT * FROM mentees";
  private MemberMapper memberMapper;
  private MenteeMapper menteeMapper;
  private PostgresMenteeRepository repository;
  private JdbcTemplate jdbc;

  @BeforeEach
  void setup() {
    jdbc = mock(JdbcTemplate.class);
    menteeMapper = mock(MenteeMapper.class);
    memberMapper = mock(MemberMapper.class);
    var validator = mock(Validator.class);
    when(validator.validate(any())).thenReturn(Collections.emptySet());
    repository =
        spy(
            new PostgresMenteeRepository(
                jdbc,
                menteeMapper,
                memberMapper,
                mock(com.wcc.platform.repository.MemberRepository.class),
                validator));
  }

  @Test
  void testCreate() {
    var mentee = createMenteeTest();
    when(memberMapper.addMember(any())).thenReturn(1L);
    doReturn(Optional.of(mentee)).when(repository).findById(1L);

    Mentee result = repository.create(mentee);

    assertNotNull(result);
    assertEquals("Mentee bio", result.getBio());
    assertEquals("ACTIVE", result.getProfileStatus().toString());
    assertEquals("Company name", result.getCompanyName());
    assertEquals("member@wcc.com", result.getEmail());
    assertEquals("City", result.getCity());
    assertEquals(2, result.getSkills().yearsExperience());
    assertEquals("Spain", result.getCountry().countryName());
    assertEquals(
        List.of(TechnicalArea.BACKEND, TechnicalArea.FRONTEND),
        result.getSkills().areas().stream().map(TechnicalAreaProficiency::technicalArea).toList());
    assertEquals(
        List.of(Languages.JAVASCRIPT),
        result.getSkills().languages().stream().map(LanguageProficiency::language).toList());
    assertEquals(
        List.of(MentorshipFocusArea.GROW_BEGINNER_TO_MID), result.getSkills().mentorshipFocus());
  }

  @Test
  void testFindById() throws Exception {
    Long menteeId = 1L;
    Mentee mentee = mock(Mentee.class);
    ResultSet resultSet = mock(ResultSet.class);

    when(jdbc.query(eq(SQL_GET_BY_ID), any(ResultSetExtractor.class), eq(menteeId)))
        .thenAnswer(
            invocation -> {
              ResultSetExtractor<Optional<Mentee>> extractor = invocation.getArgument(1);

              when(resultSet.next()).thenReturn(true);
              when(menteeMapper.mapRowToMentee(resultSet)).thenReturn(mentee);

              return extractor.extractData(resultSet);
            });

    Optional<Mentee> result = repository.findById(menteeId);

    assertTrue(result.isPresent());
    assertEquals(mentee, result.get());
    verify(menteeMapper).mapRowToMentee(resultSet);
  }

  @Test
  void testGetAll() throws Exception {
    Mentee mentee1 = mock(Mentee.class);
    Mentee mentee2 = mock(Mentee.class);

    when(jdbc.query(eq(SELECT_ALL_MENTEES), any(RowMapper.class)))
        .thenAnswer(
            invocation -> {
              RowMapper<Mentee> rowMapper = invocation.getArgument(1);

              ResultSet resultSet = mock(ResultSet.class);
              when(resultSet.getLong(anyString())).thenReturn(1L, 2L);

              return List.of(rowMapper.mapRow(resultSet, 0), rowMapper.mapRow(resultSet, 1));
            });

    when(menteeMapper.mapRowToMentee(any(ResultSet.class))).thenReturn(mentee1, mentee2);

    List<Mentee> result = repository.getAll();

    assertEquals(2, result.size());
    assertEquals(mentee1, result.get(0));
    assertEquals(mentee2, result.get(1));
  }
}
