package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMenteeFactories.createMenteeTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.repository.MenteeRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MenteeServiceTest {

    @Mock private MenteeRepository menteeRepository;

    @InjectMocks private MenteeService menteeService;

    private Mentee mentee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mentee = createMenteeTest();
    }

    @Test
    @DisplayName("Given Mentee When created Then should return created mentee")
    void testCreateMentee() {
        when(menteeRepository.create(any(Mentee.class))).thenReturn(mentee);

        Member result = menteeService.create(mentee);

        assertEquals(mentee, result);
        verify(menteeRepository).create(mentee);
    }

    @Test
    @DisplayName("Given has mentees When getting all mentees Then should return all")
    void testGetAllMentees() {
        List<Mentee> mentees = List.of(mentee);
        when(menteeRepository.getAll()).thenReturn(mentees);

        List<Mentee> result = menteeService.getAllMentees();

        assertEquals(mentees, result);
        verify(menteeRepository).getAll();
    }
}
