package com.wcc.platform.repository.postgres.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.repository.SkillRepository;
import com.wcc.platform.repository.postgres.PostgresCountryRepository;
import com.wcc.platform.repository.postgres.PostgresMemberRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

class MenteeMapperTest {

  private static final String COLUMN_MENTEE_ID = "mentee_id";
  private static final String COLUMN_PROFILE_STATUS = "mentees_profile_status";
  private static final String COLUMN_BIO = "bio";
  private static final String COLUMN_SPOKEN_LANGUAGES = "spoken_languages";

  @Mock private JdbcTemplate jdbc;
  @Mock private ResultSet resultSet;
  @Mock private PostgresMemberRepository memberRepository;
  @Mock private SkillRepository skillsRepository;
  @Mock private PostgresCountryRepository countryRepository;

  @InjectMocks private MenteeMapper menteeMapper;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    menteeMapper = spy(new MenteeMapper(jdbc, memberRepository, skillsRepository));
  }

  @Test
  void testMapRowToMenteeSuccessfully() throws Exception {
    // Arrange
    long menteeId = 2L;
    Member member = mock(Member.class);
    when(resultSet.getLong(COLUMN_MENTEE_ID)).thenReturn(menteeId);
    when(resultSet.getInt(COLUMN_PROFILE_STATUS)).thenReturn(1);
    when(resultSet.getString(COLUMN_BIO)).thenReturn("Looking for a mentor");
    when(resultSet.getString(COLUMN_SPOKEN_LANGUAGES)).thenReturn("German");

    when(memberRepository.findById(menteeId)).thenReturn(Optional.of(member));

    // Act
    Mentee mentee = menteeMapper.mapRowToMentee(resultSet);

    // Assert
    assertEquals(menteeId, mentee.getId());
    assertEquals(ProfileStatus.fromId(1), mentee.getProfileStatus());
    assertThat(mentee.getSpokenLanguages()).containsExactlyInAnyOrderElementsOf(List.of("German"));
    assertEquals("Looking for a mentor", mentee.getBio());
  }

  @Test
  void testMapRowToMenteeThrowsExceptionOnSqlError() throws Exception {
    // Arrange
    when(resultSet.getLong(COLUMN_MENTEE_ID)).thenThrow(new SQLException("DB error"));

    // Act & Assert
    SQLException exception =
        assertThrows(
            SQLException.class,
            () -> {
              menteeMapper.mapRowToMentee(resultSet);
            });

    assertEquals("DB error", exception.getMessage());
  }
}
