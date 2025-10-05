package com.wcc.platform.repository.postgres.component;

import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_BIO;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_MENTOR_ID;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_PROFILE_STATUS;
import static com.wcc.platform.repository.postgres.constants.MentorConstants.COLUMN_SPOKEN_LANG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.cms.pages.mentorship.MenteeSection;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.repository.postgres.PostgresMemberRepository;
import com.wcc.platform.repository.postgres.PostgresMenteeSectionRepository;
import com.wcc.platform.repository.postgres.PostgresSkillRepository;
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

class MentorMapperTest {

  @Mock private JdbcTemplate jdbc;
  @Mock private ResultSet resultSet;
  @Mock private PostgresMemberRepository memberRepository;
  @Mock private PostgresSkillRepository skillsRepository;
  @Mock private PostgresMenteeSectionRepository menteeSectionRepository;

  @InjectMocks private MentorMapper mentorMapper;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    mentorMapper =
        spy(new MentorMapper(jdbc, memberRepository, skillsRepository, menteeSectionRepository));
  }

  @Test
  void mapsRowToMentorSuccessfully() throws Exception {
    long mentorId = 1L;
    Member member = mock(Member.class);
    Skills skills = mock(Skills.class);
    MenteeSection menteeSection = mock(MenteeSection.class);

    when(resultSet.getLong(COLUMN_MENTOR_ID)).thenReturn(mentorId);
    when(resultSet.getInt(COLUMN_PROFILE_STATUS)).thenReturn(1);
    when(resultSet.getString(COLUMN_SPOKEN_LANG)).thenReturn("English,Spanish");
    when(resultSet.getString(COLUMN_BIO)).thenReturn("Experienced mentor");
    when(memberRepository.findById(mentorId)).thenReturn(Optional.of(member));
    when(skillsRepository.findByMentorId(mentorId)).thenReturn(Optional.of(skills));
    when(menteeSectionRepository.findByMentorId(mentorId)).thenReturn(Optional.of(menteeSection));

    Mentor mentor = mentorMapper.mapRowToMentor(resultSet);

    assertEquals(mentorId, mentor.getId());
    assertEquals(mentor.getProfileStatus(), ProfileStatus.fromId(1));
    assertThat(mentor.getSpokenLanguages())
        .containsExactlyInAnyOrderElementsOf(List.of("English", "Spanish"));
    assertEquals(mentor.getSkills(), skills);
    assertEquals("Experienced mentor", mentor.getBio());
    assertEquals(mentor.getMenteeSection(), menteeSection);
  }

  @Test
  void handlesSqlExceptionGracefully() throws Exception {
    when(resultSet.getLong(COLUMN_MENTOR_ID)).thenThrow(SQLException.class);
    assertThrows(SQLException.class, () -> mentorMapper.mapRowToMentor(resultSet));
  }
}
