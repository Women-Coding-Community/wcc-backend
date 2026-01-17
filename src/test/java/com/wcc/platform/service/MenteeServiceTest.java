package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMenteeFactories.createMenteeTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.configuration.MentorshipConfig;
import com.wcc.platform.domain.exceptions.InvalidMentorshipTypeException;
import com.wcc.platform.domain.exceptions.MentorshipCycleClosedException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycle;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MenteeServiceTest {

    @Mock private MenteeRepository menteeRepository;
    @Mock private MentorshipService mentorshipService;
    @Mock private MentorshipConfig mentorshipConfig;
    @Mock private MentorshipConfig.Validation validation;
    @Mock private MentorshipCycleRepository cycleRepository;

    private MenteeService menteeService;

    private Mentee mentee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mentorshipConfig.getValidation()).thenReturn(validation);
        when(validation.isEnabled()).thenReturn(true);
        menteeService = new MenteeService(menteeRepository, mentorshipService, mentorshipConfig, cycleRepository);
        mentee = createMenteeTest();
    }

    @Test
    @DisplayName("Given Mentee When created Then should return created mentee")
    void testCreateMentee() {
        Mentee validMentee = Mentee.menteeBuilder()
            .id(1L)
            .fullName("Test Mentee")
            .email("test@example.com")
            .position("Software Engineer")
            .country(mentee.getCountry())
            .city("Test City")
            .companyName("Test Company")
            .images(mentee.getImages())
            .profileStatus(ProfileStatus.ACTIVE)
            .bio("Test bio")
            .spokenLanguages(List.of("English"))
            .skills(mentee.getSkills())
            .mentorshipType(MentorshipType.AD_HOC)
            .build();

        MentorshipCycle openCycle = new MentorshipCycle(MentorshipType.AD_HOC, Month.MAY);
        when(mentorshipService.getCurrentCycle()).thenReturn(openCycle);
        when(menteeRepository.create(any(Mentee.class), any(Integer.class))).thenReturn(validMentee);

        Member result = menteeService.create(validMentee);

        assertEquals(validMentee, result);
        verify(menteeRepository).create(any(Mentee.class), any(Integer.class));
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

    @Test
    @DisplayName("Given closed cycle When creating mentee Then should throw MentorshipCycleClosedException")
    void shouldThrowExceptionWhenCycleIsClosed() {
        when(mentorshipService.getCurrentCycle()).thenReturn(MentorshipService.CYCLE_CLOSED);

        MentorshipCycleClosedException exception = assertThrows(
            MentorshipCycleClosedException.class,
            () -> menteeService.create(mentee)
        );

        assertThat(exception.getMessage())
            .contains("Mentorship cycle is currently closed");
    }

    @Test
    @DisplayName("Given mentee type does not match cycle type When creating mentee Then should throw InvalidMentorshipTypeException")
    void shouldThrowExceptionWhenMenteeTypeDoesNotMatchCycleType() {
        Mentee adHocMentee = Mentee.menteeBuilder()
            .id(1L)
            .fullName("Test Mentee")
            .email("test@example.com")
            .position("Software Engineer")
            .country(mentee.getCountry())
            .city("Test City")
            .companyName("Test Company")
            .images(mentee.getImages())
            .profileStatus(ProfileStatus.ACTIVE)
            .bio("Test bio")
            .spokenLanguages(List.of("English"))
            .skills(mentee.getSkills())
            .mentorshipType(MentorshipType.AD_HOC)
            .build();

        MentorshipCycle longTermCycle = new MentorshipCycle(MentorshipType.LONG_TERM, Month.MARCH);
        when(mentorshipService.getCurrentCycle()).thenReturn(longTermCycle);

        InvalidMentorshipTypeException exception = assertThrows(
            InvalidMentorshipTypeException.class,
            () -> menteeService.create(adHocMentee)
        );

        assertThat(exception.getMessage())
            .contains("Mentee mentorship type 'Ad-Hoc' does not match current cycle type 'Long-Term'");
    }

    @Test
    @DisplayName("Given valid cycle and matching mentee type When creating mentee Then should create successfully")
    void shouldCreateMenteeWhenCycleIsOpenAndTypeMatches() {
        Mentee adHocMentee = Mentee.menteeBuilder()
            .id(1L)
            .fullName("Test Mentee")
            .email("test@example.com")
            .position("Software Engineer")
            .country(mentee.getCountry())
            .city("Test City")
            .companyName("Test Company")
            .images(mentee.getImages())
            .profileStatus(ProfileStatus.ACTIVE)
            .bio("Test bio")
            .spokenLanguages(List.of("English"))
            .skills(mentee.getSkills())
            .mentorshipType(MentorshipType.AD_HOC)
            .build();

        MentorshipCycle adHocCycle = new MentorshipCycle(MentorshipType.AD_HOC, Month.MAY);
        when(mentorshipService.getCurrentCycle()).thenReturn(adHocCycle);
        when(menteeRepository.create(any(Mentee.class), any(Integer.class))).thenReturn(adHocMentee);

        Member result = menteeService.create(adHocMentee);

        assertThat(result).isEqualTo(adHocMentee);
        verify(menteeRepository).create(any(Mentee.class), any(Integer.class));
        verify(mentorshipService).getCurrentCycle();
    }

    @Test
    @DisplayName("Given validation is disabled When creating mentee Then should skip validation and create successfully")
    void shouldSkipValidationWhenValidationIsDisabled() {
        when(validation.isEnabled()).thenReturn(false);

        Mentee adHocMentee = Mentee.menteeBuilder()
            .id(1L)
            .fullName("Test Mentee")
            .email("test@example.com")
            .position("Software Engineer")
            .country(mentee.getCountry())
            .city("Test City")
            .companyName("Test Company")
            .images(mentee.getImages())
            .profileStatus(ProfileStatus.ACTIVE)
            .bio("Test bio")
            .spokenLanguages(List.of("English"))
            .skills(mentee.getSkills())
            .mentorshipType(MentorshipType.AD_HOC)
            .build();

        when(menteeRepository.create(any(Mentee.class), any(Integer.class))).thenReturn(adHocMentee);

        Member result = menteeService.create(adHocMentee);

        assertThat(result).isEqualTo(adHocMentee);
        verify(menteeRepository).create(any(Mentee.class), any(Integer.class));
        verify(mentorshipService, never()).getCurrentCycle();
    }
}
