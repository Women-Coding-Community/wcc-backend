package com.wcc.platform.repository.postgres.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.repository.SkillRepository;
import com.wcc.platform.repository.postgres.PostgresCountryRepository;
import com.wcc.platform.repository.postgres.PostgresMemberRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
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
        //Arrange
        long menteeId = 2L;
        Member member = mock(Member.class);
        when(resultSet.getLong(COLUMN_MENTEE_ID)).thenReturn(menteeId);
        when(resultSet.getInt(COLUMN_PROFILE_STATUS)).thenReturn(1);
        when(resultSet.getString(COLUMN_BIO)).thenReturn("Looking for a mentor");
        when(resultSet.getString(COLUMN_SPOKEN_LANGUAGES)).thenReturn("German");

        when(memberRepository.findById(menteeId)).thenReturn(Optional.of(member));
        when(menteeMapper.loadMentorshipTypes(menteeId)).thenReturn(Optional.of(MentorshipType.fromId(1)));

        //Act
        Mentee mentee = menteeMapper.mapRowToMentee(resultSet);

        //Assert
        assertEquals(menteeId, mentee.getId());
        assertEquals(ProfileStatus.fromId(1), mentee.getProfileStatus());
        assertThat(mentee.getSpokenLanguages())
            .containsExactlyInAnyOrderElementsOf(List.of("German"));
        assertEquals("Looking for a mentor", mentee.getBio());
        assertEquals("Ad-Hoc", mentee.getMentorshipType().toString());
    }

    @Test
    void testAddMentee() {
        //Arrange
        Member member = mock(Member.class);
        Long memberId = 5L;
        when(member.getId()).thenReturn(memberId);

        Mentee mentee = mock(Mentee.class);
        when(mentee.getFullName()).thenReturn("Jane Doe");
        when(mentee.getSlackDisplayName()).thenReturn("jane");
        when(mentee.getPosition()).thenReturn("QA");
        when(mentee.getCompanyName()).thenReturn("WCC");
        when(mentee.getEmail()).thenReturn("jane@example.com");
        when(mentee.getCity()).thenReturn("Amsterdam");
        when(mentee.getBio()).thenReturn("Looking for a mentor");
        when(mentee.getImages()).thenReturn(Collections.emptyList());
        when(mentee.getMemberTypes()).thenReturn(Collections.emptyList());
        when(mentee.getNetwork()).thenReturn(Collections.emptyList());

        Country country = mock(Country.class);
        when(mentee.getCountry()).thenReturn(country);
        when(countryRepository.findCountryIdByCode(anyString())).thenReturn(3L);

        ProfileStatus profileStatus = mock(ProfileStatus.class);
        when(mentee.getProfileStatus()).thenReturn(profileStatus);
        when(profileStatus.getStatusId()).thenReturn(1);

        Skills skills = mock(Skills.class);
        when(mentee.getSkills()).thenReturn(skills);
        when(skills.yearsExperience()).thenReturn(5);
        when(skills.areas()).thenReturn(Collections.emptyList());
        when(skills.languages()).thenReturn(Collections.emptyList());
        when(skills.mentorshipFocus()).thenReturn(Collections.emptyList());

        MentorshipType mentorshipType = mock(MentorshipType.class);
        when(mentee.getMentorshipType()).thenReturn(mentorshipType);
        when(mentorshipType.getMentorshipTypeId()).thenReturn(10);

        MentorshipType prevMentorshipType = mock(MentorshipType.class);
        when(mentee.getPrevMentorshipType()).thenReturn(prevMentorshipType);
        when(prevMentorshipType.getMentorshipTypeId()).thenReturn(20);

        TechnicalArea techArea = mock(TechnicalArea.class);
        when(techArea.getTechnicalAreaId()).thenReturn(100);
        when(skills.areas()).thenReturn(List.of(techArea));

        Languages lang = mock(Languages.class);
        when(lang.getLangId()).thenReturn(55);
        when(skills.languages()).thenReturn(List.of(lang));

        //Act
        menteeMapper.addMentee(mentee, memberId);

        //Assert
        verify(jdbc).update(
            eq("INSERT INTO mentees (mentee_id, mentees_profile_status, bio, years_experience, spoken_languages) VALUES (?, ?, ?, ?, ?)"),
            eq(memberId),
            eq(1),
            eq("Looking for a mentor"),
            eq(5),
            eq("")
        );

        verify(jdbc).update(
            eq("INSERT INTO mentee_technical_areas (mentee_id, technical_area_id) VALUES (?, ?)"),
            eq(memberId),
            eq(100)
        );

        verify(jdbc).update(
            eq("INSERT INTO mentee_languages (mentee_id, language_id) VALUES (?, ?)"),
            eq(memberId),
            eq(55)
        );

        verify(jdbc).update(
            eq("INSERT INTO mentee_mentorship_types (mentee_id, mentorship_type) VALUES (?, ?)"),
            eq(memberId),
            eq(10)
        );

        verify(jdbc).update(
            eq("INSERT INTO mentee_previous_mentorship_types (mentee_id, mentorship_type) VALUES (?, ?)"),
            eq(memberId),
            eq(20)
        );
    }

    @Test
    void testMapRowToMenteeThrowsExceptionOnSqlError() throws Exception {
        // Arrange
        when(resultSet.getLong(COLUMN_MENTEE_ID)).thenThrow(new SQLException("DB error"));

        // Act & Assert
        SQLException exception = assertThrows(SQLException.class, () -> {
            menteeMapper.mapRowToMentee(resultSet);
        });

        assertEquals("DB error", exception.getMessage());
    }


}
