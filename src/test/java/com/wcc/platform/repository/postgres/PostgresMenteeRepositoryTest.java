package com.wcc.platform.repository.postgres;

import static com.wcc.platform.factories.SetupMenteeFactories.createMenteeTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.repository.postgres.component.MemberMapper;
import com.wcc.platform.repository.postgres.component.MenteeMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class PostgresMenteeRepositoryTest {
    private JdbcTemplate jdbc;
    private MemberMapper memberMapper;
    private MenteeMapper menteeMapper;
    private PostgresMenteeRepository repository;

    @BeforeEach
    void setup() {
        jdbc = mock(JdbcTemplate.class);
        menteeMapper = mock(MenteeMapper.class);
        memberMapper = mock(MemberMapper.class);
        repository = spy(new PostgresMenteeRepository(jdbc, menteeMapper, memberMapper));
    }

    @Test
    void testCreate() {
        var mentee = createMenteeTest();
        when(memberMapper.addMember(any())).thenReturn(1L);
        doNothing().when(menteeMapper).addMentee(any(), eq(1L));
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
        assertEquals(List.of(TechnicalArea.BACKEND, TechnicalArea.FRONTEND), result.getSkills().areas());
        assertEquals(List.of(Languages.JAVASCRIPT), result.getSkills().languages());
        assertEquals(List.of(MentorshipFocusArea.GROW_BEGINNER_TO_MID), result.getSkills().mentorshipFocus());
    }
}
