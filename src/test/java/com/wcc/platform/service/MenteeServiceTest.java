package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMenteeFactories.createMenteeTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.configuration.MentorshipConfig;
import com.wcc.platform.domain.exceptions.InvalidMentorshipTypeException;
import com.wcc.platform.domain.exceptions.MenteeRegistrationLimitExceededException;
import com.wcc.platform.domain.exceptions.MentorshipCycleClosedException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeRegistration;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycle;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MenteeServiceTest {

  @Mock private MenteeApplicationRepository applicationRepository;
  @Mock private MenteeRepository menteeRegistrationRepository;
  @Mock private MentorshipService mentorshipService;
  @Mock private MentorshipConfig mentorshipConfig;
  @Mock private MentorshipConfig.Validation validation;
  @Mock private MentorshipCycleRepository cycleRepository;
  @Mock private MemberRepository memberRepository;

  private MenteeService menteeService;
  private Mentee mentee;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(mentorshipConfig.getValidation()).thenReturn(validation);
    when(validation.isEnabled()).thenReturn(true);
    menteeService =
        new MenteeService(
            mentorshipService,
            mentorshipConfig,
            cycleRepository,
            applicationRepository,
            menteeRegistrationRepository);
    mentee = createMenteeTest();
  }

  @Test
  @DisplayName("Given Mentee Registration When saved Then should return mentee")
  void testSaveRegistrationMentee() {
    var currentYear = java.time.Year.now();
    MenteeRegistration registration =
        new MenteeRegistration(mentee, MentorshipType.AD_HOC, currentYear, List.of(1L));

    MentorshipCycleEntity cycle =
        MentorshipCycleEntity.builder()
            .cycleId(1L)
            .cycleYear(currentYear)
            .mentorshipType(MentorshipType.AD_HOC)
            .status(CycleStatus.OPEN)
            .build();

    when(cycleRepository.findByYearAndType(currentYear, MentorshipType.AD_HOC))
        .thenReturn(Optional.of(cycle));

    Mentee result = menteeService.saveRegistration(registration);

    assertEquals(mentee, result);
    verify(menteeRegistrationRepository).create(any(Mentee.class));
    verify(applicationRepository).create(any());
  }

  @Test
  @DisplayName(
      "Given mentee exceeds registration limit When creating mentee Then should throw MenteeRegistrationLimitExceededException")
  void shouldThrowExceptionWhenRegistrationLimitExceeded() {
    var currentYear = java.time.Year.now();
    Mentee menteeWithId =
        Mentee.menteeBuilder()
            .id(1L)
            .fullName("Mentee")
            .email("a@b.com")
            .position("pos")
            .slackDisplayName("slack")
            .country(mentee.getCountry())
            .city("city")
            .profileStatus(ProfileStatus.ACTIVE)
            .bio("bio")
            .skills(mentee.getSkills())
            .spokenLanguages(List.of("English"))
            .build();
    MenteeRegistration registration =
        new MenteeRegistration(menteeWithId, MentorshipType.AD_HOC, currentYear, List.of(1L));

    MentorshipCycleEntity cycle =
        MentorshipCycleEntity.builder()
            .cycleId(1L)
            .cycleYear(currentYear)
            .mentorshipType(MentorshipType.AD_HOC)
            .status(CycleStatus.OPEN)
            .build();

    when(cycleRepository.findByYearAndType(currentYear, MentorshipType.AD_HOC))
        .thenReturn(Optional.of(cycle));
    when(applicationRepository.countMenteeApplications(1L, 1L)).thenReturn(5L);

    MenteeRegistrationLimitExceededException exception =
        assertThrows(
            MenteeRegistrationLimitExceededException.class,
            () -> menteeService.saveRegistration(registration));

    assertThat(exception.getMessage()).contains("has already reached the limit of 5 registrations");
  }

  @Test
  @DisplayName("Given has mentees When getting all mentees Then should return all")
  void testGetAllMentees() {
    List<Mentee> mentees = List.of(mentee);
    when(menteeRegistrationRepository.getAll()).thenReturn(mentees);

    List<Mentee> result = menteeService.getAllMentees();

    assertEquals(mentees, result);
    verify(menteeRegistrationRepository).getAll();
  }

  @Test
  @DisplayName(
      "Given closed cycle When creating mentee Then should throw MentorshipCycleClosedException")
  void shouldThrowExceptionWhenCycleIsClosed() {
    var currentYear = java.time.Year.now();
    MenteeRegistration registration =
        new MenteeRegistration(mentee, MentorshipType.AD_HOC, currentYear, List.of(1L));
    when(mentorshipService.getCurrentCycle()).thenReturn(MentorshipService.CYCLE_CLOSED);

    MentorshipCycleClosedException exception =
        assertThrows(
            MentorshipCycleClosedException.class,
            () -> menteeService.saveRegistration(registration));

    assertThat(exception.getMessage()).contains("Mentorship cycle is currently closed");
  }

  @Test
  @DisplayName(
      "Given mentee type does not match cycle type When creating mentee Then should throw InvalidMentorshipTypeException")
  void shouldThrowExceptionWhenMenteeTypeDoesNotMatchCycleType() {
    var currentYear = java.time.Year.now();
    MenteeRegistration registration =
        new MenteeRegistration(mentee, MentorshipType.AD_HOC, currentYear, List.of(1L));

    MentorshipCycle longTermCycle = new MentorshipCycle(MentorshipType.LONG_TERM, Month.MARCH);
    when(mentorshipService.getCurrentCycle()).thenReturn(longTermCycle);

    InvalidMentorshipTypeException exception =
        assertThrows(
            InvalidMentorshipTypeException.class,
            () -> menteeService.saveRegistration(registration));

    assertThat(exception.getMessage())
        .contains("Mentee mentorship type 'Ad-Hoc' does not match current cycle type 'Long-Term'");
  }

  @Test
  @DisplayName(
      "Given valid cycle and matching mentee type When creating mentee Then should create successfully")
  void shouldSaveRegistrationMenteeWhenCycleIsOpenAndTypeMatches() {
    var currentYear = java.time.Year.now();
    MenteeRegistration registration =
        new MenteeRegistration(mentee, MentorshipType.AD_HOC, currentYear, List.of(1L));

    MentorshipCycle adHocCycle = new MentorshipCycle(MentorshipType.AD_HOC, Month.MAY);
    when(mentorshipService.getCurrentCycle()).thenReturn(adHocCycle);
    when(menteeRegistrationRepository.create(any(Mentee.class))).thenReturn(mentee);

    Member result = menteeService.saveRegistration(registration);

    assertThat(result).isEqualTo(mentee);
    verify(menteeRegistrationRepository).create(any(Mentee.class));
    verify(mentorshipService).getCurrentCycle();
  }

  @Test
  @DisplayName(
      "Given validation is disabled When creating mentee Then should skip validation and create successfully")
  void shouldSkipValidationWhenValidationIsDisabled() {
    var currentYear = java.time.Year.now();
    MenteeRegistration registration =
        new MenteeRegistration(mentee, MentorshipType.AD_HOC, currentYear, List.of(1L));
    when(validation.isEnabled()).thenReturn(false);

    when(cycleRepository.findByYearAndType(any(), any())).thenReturn(Optional.empty());
    when(mentorshipService.getCurrentCycle())
        .thenReturn(new MentorshipCycle(MentorshipType.AD_HOC, Month.JANUARY));
    when(menteeRegistrationRepository.create(any())).thenReturn(mentee);
    when(applicationRepository.countMenteeApplications(any(), any())).thenReturn(0L);

    Member result = menteeService.saveRegistration(registration);

    assertThat(result).isEqualTo(mentee);
    verify(menteeRegistrationRepository).create(any(Mentee.class));
    verify(mentorshipService).getCurrentCycle();
  }
}
