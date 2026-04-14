package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMenteeFactories.createMenteeTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.DuplicatedPriorityException;
import com.wcc.platform.domain.exceptions.InvalidMentorshipTypeException;
import com.wcc.platform.domain.exceptions.MenteeRegistrationLimitException;
import com.wcc.platform.domain.exceptions.MentorNotFoundException;
import com.wcc.platform.domain.exceptions.MentorshipCycleClosedException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.MenteeApplicationDto;
import com.wcc.platform.domain.platform.mentorship.MenteeRegistration;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MenteeServiceTest {

  @Mock private MenteeApplicationRepository applicationRepository;
  @Mock private MenteeRepository menteeRepository;
  @Mock private MentorshipCycleRepository cycleRepository;
  @Mock private MemberRepository memberRepository;
  @Mock private MentorRepository mentorRepository;
  @Mock private UserProvisionService userProvisionService;

  private MenteeService menteeService;
  private Mentee mentee;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    menteeService =
        new MenteeService(
            cycleRepository,
            applicationRepository,
            menteeRepository,
            memberRepository,
            mentorRepository,
            userProvisionService);
    mentee = createMenteeTest(null, "Test Mentee", "test@wcc.com");
    when(mentorRepository.findById(any())).thenReturn(Optional.of(Mentor.mentorBuilder().build()));

    var openCycle =
        MentorshipCycleEntity.builder()
            .cycleId(1L)
            .status(CycleStatus.OPEN)
            .mentorshipType(MentorshipType.AD_HOC)
            .build();
    when(cycleRepository.findOpenCycle()).thenReturn(Optional.of(openCycle));
  }

  @Test
  @DisplayName("Given mentee registration, when saved, then should return mentee")
  void testSaveRegistrationMentee() {
    var currentYear = Year.now();
    var registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.AD_HOC,
            currentYear,
            List.of(
                new MenteeApplicationDto(1L, 1, "Test application message", "Test why mentor")));

    var cycle =
        MentorshipCycleEntity.builder()
            .cycleId(1L)
            .cycleYear(currentYear)
            .mentorshipType(MentorshipType.AD_HOC)
            .status(CycleStatus.OPEN)
            .build();

    Member existingMember = Member.builder().id(1L).build();
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(existingMember));
    when(menteeRepository.create(any(Mentee.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(menteeRepository.update(any(), any(Mentee.class)))
        .thenAnswer(invocation -> invocation.getArgument(1));
    when(menteeRepository.findById(any()))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(mentee));
    when(cycleRepository.findOpenCycle()).thenReturn(Optional.of(cycle));
    when(applicationRepository.findByMenteeAndCycle(any(), any())).thenReturn(List.of());

    Mentee result = menteeService.saveRegistration(registration);

    assertEquals(mentee, result);
    verify(userProvisionService).provisionUserRole(any(), anyString(), eq(RoleType.MENTEE));
    verify(memberRepository, atLeastOnce()).findByEmail(anyString());
    verify(menteeRepository).create(any(Mentee.class));
    verify(applicationRepository).create(any());
  }

  @Test
  @DisplayName(
      "Given mentee exceeds registration limit, when creating mentee, "
          + "then should throw MenteeRegistrationLimitExceededException")
  void shouldThrowExceptionWhenRegistrationLimitExceeded() {
    var currentYear = Year.now();
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
        new MenteeRegistration(
            menteeWithId,
            MentorshipType.AD_HOC,
            currentYear,
            List.of(
                new MenteeApplicationDto(1L, 1, "Test application message", "Test why mentor")));

    MentorshipCycleEntity cycle =
        MentorshipCycleEntity.builder()
            .cycleId(1L)
            .cycleYear(currentYear)
            .mentorshipType(MentorshipType.AD_HOC)
            .status(CycleStatus.OPEN)
            .build();

    when(menteeRepository.findById(1L)).thenReturn(Optional.of(menteeWithId));
    when(cycleRepository.findOpenCycle()).thenReturn(Optional.of(cycle));
    when(applicationRepository.findByMenteeAndCycle(any(), any())).thenReturn(List.of());
    when(applicationRepository.countMenteeApplications(1L, 1L)).thenReturn(5L);

    MenteeRegistrationLimitException exception =
        assertThrows(
            MenteeRegistrationLimitException.class,
            () -> menteeService.saveRegistration(registration));

    assertThat(exception.getMessage()).contains("has already reached the limit of 5 registrations");
  }

  @Test
  @DisplayName(
      "Given mentee with existing priority, when creating mentee, "
          + "then should throw DuplicatedPriorityException")
  void shouldThrowExceptionWhenPriorityAlreadyExists() {
    var currentYear = Year.now();
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
        new MenteeRegistration(
            menteeWithId,
            MentorshipType.AD_HOC,
            currentYear,
            List.of(
                new MenteeApplicationDto(2L, 1, "Test application message", "Test why mentor")));

    MentorshipCycleEntity cycle =
        MentorshipCycleEntity.builder()
            .cycleId(1L)
            .cycleYear(currentYear)
            .mentorshipType(MentorshipType.AD_HOC)
            .status(CycleStatus.OPEN)
            .build();

    when(menteeRepository.findById(1L)).thenReturn(Optional.of(menteeWithId));
    when(cycleRepository.findOpenCycle()).thenReturn(Optional.of(cycle));

    // Simulate existing application with priority 1
    var existingApplication =
        MenteeApplication.builder().menteeId(1L).mentorId(1L).cycleId(1L).priorityOrder(1).build();

    when(applicationRepository.findByMenteeAndCycle(1L, 1L))
        .thenReturn(List.of(existingApplication));

    assertThrows(
        DuplicatedPriorityException.class, () -> menteeService.saveRegistration(registration));
  }

  @Test
  @DisplayName(
      "Given multiple applications with same priority in request, when creating mentee, "
          + "then should throw DuplicatedPriorityException")
  void shouldThrowExceptionWhenPriorityDuplicatedInRequest() {
    var currentYear = Year.now();
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
        new MenteeRegistration(
            menteeWithId,
            MentorshipType.AD_HOC,
            currentYear,
            List.of(
                new MenteeApplicationDto(2L, 1, "Msg 1", "Why 1"),
                new MenteeApplicationDto(3L, 1, "Msg 2", "Why 2")));

    MentorshipCycleEntity cycle =
        MentorshipCycleEntity.builder()
            .cycleId(1L)
            .cycleYear(currentYear)
            .mentorshipType(MentorshipType.AD_HOC)
            .status(CycleStatus.OPEN)
            .registrationStartDate(LocalDate.now().minusDays(1))
            .registrationEndDate(LocalDate.now().plusDays(1))
            .build();

    when(menteeRepository.findById(1L)).thenReturn(Optional.of(menteeWithId));
    when(cycleRepository.findOpenCycle()).thenReturn(Optional.of(cycle));

    when(applicationRepository.findByMenteeAndCycle(1L, 1L)).thenReturn(List.of());

    assertThrows(
        DuplicatedPriorityException.class, () -> menteeService.saveRegistration(registration));
  }

  @Test
  @DisplayName(
      "Given active mentees exist, when getting all mentees, then should return only active mentees")
  void shouldReturnOnlyActiveMentees() {
    var mentees = List.of(mentee);
    when(menteeRepository.findByStatus(ProfileStatus.ACTIVE)).thenReturn(mentees);

    var result = menteeService.getAllMentees();

    assertEquals(mentees, result);
    verify(menteeRepository).findByStatus(ProfileStatus.ACTIVE);
  }

  @Test
  @DisplayName(
      "Given closed cycle, when creating mentee, then should throw MentorshipCycleClosedException")
  void shouldThrowExceptionWhenCycleIsClosed() {
    var currentYear = Year.now();
    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.AD_HOC,
            currentYear,
            List.of(
                new MenteeApplicationDto(1L, 1, "Test application message", "Test why mentor")));
    when(cycleRepository.findOpenCycle()).thenReturn(Optional.empty());

    MentorshipCycleClosedException exception =
        assertThrows(
            MentorshipCycleClosedException.class,
            () -> menteeService.saveRegistration(registration));

    assertThat(exception.getMessage()).contains("Mentorship cycle is closed");
  }

  @Test
  @DisplayName(
      "Given mentee type does not match cycle type, when creating mentee, "
          + "then should throw InvalidMentorshipTypeException")
  void shouldThrowExceptionWhenMenteeTypeDoesNotMatchCycleType() {
    var currentYear = Year.now();
    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.AD_HOC,
            currentYear,
            List.of(
                new MenteeApplicationDto(1L, 1, "Test application message", "Test why mentor")));

    when(cycleRepository.findOpenCycle())
        .thenReturn(
            Optional.of(
                MentorshipCycleEntity.builder()
                    .mentorshipType(MentorshipType.LONG_TERM)
                    .cycleMonth(Month.MARCH)
                    .status(CycleStatus.OPEN)
                    .build()));

    InvalidMentorshipTypeException exception =
        assertThrows(
            InvalidMentorshipTypeException.class,
            () -> menteeService.saveRegistration(registration));

    assertThat(exception.getMessage())
        .contains("Mentee mentorship type 'Ad-Hoc' does not match current cycle type 'Long-Term'");
  }

  @Test
  @DisplayName(
      "Given valid cycle and matching mentee type, "
          + "when creating mentee, then should create successfully")
  void shouldSaveRegistrationMenteeWhenCycleIsOpenAndTypeMatches() {
    var currentYear = Year.now();
    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.AD_HOC,
            currentYear,
            List.of(
                new MenteeApplicationDto(1L, 1, "Test application message", "Test why mentor")));

    var cycle =
        MentorshipCycleEntity.builder()
            .mentorshipType(MentorshipType.AD_HOC)
            .cycleMonth(Month.MAY)
            .status(CycleStatus.OPEN)
            .build();
    when(cycleRepository.findOpenCycle()).thenReturn(Optional.of(cycle));

    Member existingMember = Member.builder().id(1L).build();
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(existingMember));
    when(menteeRepository.create(any(Mentee.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(menteeRepository.update(any(), any(Mentee.class)))
        .thenAnswer(invocation -> invocation.getArgument(1));
    when(menteeRepository.findById(any()))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(mentee));
    when(applicationRepository.findByMenteeAndCycle(any(), any())).thenReturn(List.of());

    Mentee result = menteeService.saveRegistration(registration);

    assertThat(result).isEqualTo(mentee);
    verify(menteeRepository).create(any(Mentee.class));
    verify(cycleRepository, atLeastOnce()).findOpenCycle();
    verify(mentorRepository).findById(1L);
  }

  @Test
  @DisplayName(
      "Given non-existent mentor, when creating mentee, then should throw MentorNotFoundException")
  void shouldThrowExceptionWhenMentorDoesNotExist() {
    var currentYear = Year.now();
    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.AD_HOC,
            currentYear,
            List.of(
                new MenteeApplicationDto(1L, 1, "Test application message", "Test why mentor")));

    when(mentorRepository.findById(1L)).thenReturn(Optional.empty());

    MentorNotFoundException exception =
        assertThrows(
            MentorNotFoundException.class, () -> menteeService.saveRegistration(registration));

    assertThat(exception.getMessage()).isEqualTo("Mentor not found: 1");
  }

  @Test
  @DisplayName(
      "Given an open cycle matching the registration type, when creating mentee, "
          + "then should create successfully")
  void shouldCreateMenteeWhenOpenCycleMatches() {
    var currentYear = Year.now();
    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.AD_HOC,
            currentYear,
            List.of(
                new MenteeApplicationDto(1L, 1, "Test application message", "Test why mentor")));

    when(cycleRepository.findOpenCycle())
        .thenReturn(
            Optional.of(
                MentorshipCycleEntity.builder()
                    .mentorshipType(MentorshipType.AD_HOC)
                    .cycleMonth(Month.MAY)
                    .status(CycleStatus.OPEN)
                    .build()));
    Member existingMember = Member.builder().id(1L).build();
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(existingMember));
    when(menteeRepository.create(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(menteeRepository.update(any(), any())).thenAnswer(invocation -> invocation.getArgument(1));
    when(menteeRepository.findById(any()))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(mentee));
    when(applicationRepository.findByMenteeAndCycle(any(), any())).thenReturn(List.of());
    when(applicationRepository.countMenteeApplications(any(), any())).thenReturn(0L);

    Mentee result = menteeService.saveRegistration(registration);

    assertThat(result).isEqualTo(mentee);
    verify(menteeRepository).create(any(Mentee.class));
    verify(cycleRepository, atLeastOnce()).findOpenCycle();
  }

  @Test
  @DisplayName(
      "Given existing member with email, when creating mentee with same email, "
          + "then it should use existing member")
  void shouldUseExistingMemberWhenMenteeEmailAlreadyExists() {
    var currentYear = Year.now();
    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.AD_HOC,
            currentYear,
            List.of(
                new MenteeApplicationDto(1L, 1, "Test application message", "Test why mentor")));

    var cycle =
        MentorshipCycleEntity.builder()
            .cycleId(1L)
            .cycleYear(currentYear)
            .mentorshipType(MentorshipType.AD_HOC)
            .status(CycleStatus.OPEN)
            .build();

    // Mock existing member with same email
    Member existingMember = Member.builder().id(999L).email(mentee.getEmail()).build();
    Mentee menteeWithExistingId =
        Mentee.menteeBuilder()
            .id(999L)
            .fullName(mentee.getFullName())
            .email(mentee.getEmail())
            .position(mentee.getPosition())
            .slackDisplayName(mentee.getSlackDisplayName())
            .country(mentee.getCountry())
            .city(mentee.getCity())
            .profileStatus(mentee.getProfileStatus())
            .bio(mentee.getBio())
            .skills(mentee.getSkills())
            .spokenLanguages(mentee.getSpokenLanguages())
            .build();

    when(memberRepository.findByEmail(mentee.getEmail())).thenReturn(Optional.of(existingMember));
    when(cycleRepository.findOpenCycle()).thenReturn(Optional.of(cycle));
    when(applicationRepository.findByMenteeAndCycle(any(), any())).thenReturn(List.of());
    when(menteeRepository.create(any(Mentee.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(menteeRepository.update(any(), any(Mentee.class)))
        .thenAnswer(invocation -> invocation.getArgument(1));
    when(menteeRepository.findById(999L)).thenReturn(Optional.of(menteeWithExistingId));

    Mentee result = menteeService.saveRegistration(registration);

    assertThat(result.getId()).isEqualTo(999L);
    assertThat(result.getEmail()).isEqualTo(mentee.getEmail());
    verify(memberRepository, atLeastOnce()).findByEmail(mentee.getEmail());
    verify(menteeRepository).update(eq(999L), any(Mentee.class));
  }

  @Test
  @DisplayName(
      "Given member exists but id is not provided, when creating mentee with same email, "
          + "then member is returned based on respective email")
  void shouldFallbackToExistingMemberWhenProvidedIdDoesNotExistButEmailExists() {
    var currentYear = Year.now();
    var staleIdMentee = createMenteeTest(12_345L, "Test Mentee", "existing@test.com");
    var registration =
        new MenteeRegistration(
            staleIdMentee,
            MentorshipType.AD_HOC,
            currentYear,
            List.of(new MenteeApplicationDto(1L, 1, "msg", "why")));

    var cycle =
        MentorshipCycleEntity.builder()
            .cycleId(1L)
            .cycleYear(currentYear)
            .mentorshipType(MentorshipType.AD_HOC)
            .status(CycleStatus.OPEN)
            .build();

    Mentee menteeWithExistingId = createMenteeTest(999L, "Test Mentee", "existing@test.com");

    when(menteeRepository.findById(12_345L)).thenReturn(Optional.empty());
    when(memberRepository.findByEmail("existing@test.com"))
        .thenReturn(Optional.of(Member.builder().id(999L).build()));
    when(cycleRepository.findOpenCycle()).thenReturn(Optional.of(cycle));
    when(applicationRepository.findByMenteeAndCycle(any(), any())).thenReturn(List.of());
    when(applicationRepository.countMenteeApplications(any(), any())).thenReturn(0L);
    when(menteeRepository.findById(999L))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(menteeWithExistingId));
    when(menteeRepository.create(any(Mentee.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(menteeRepository.update(eq(999L), any(Mentee.class)))
        .thenAnswer(invocation -> invocation.getArgument(1));

    menteeService.saveRegistration(registration);

    verify(menteeRepository).create(any(Mentee.class));
  }

  @Test
  @DisplayName(
      "Given null cycleYear in registration, when saving, then it should default to current year")
  void shouldDefaultToCurrentYearWhenCycleYearIsNull() {
    var currentYear = Year.now();
    var registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.AD_HOC,
            null,
            List.of(
                new MenteeApplicationDto(1L, 1, "Test application message", "Test why mentor")));

    var cycle =
        MentorshipCycleEntity.builder()
            .cycleId(1L)
            .cycleYear(currentYear)
            .mentorshipType(MentorshipType.AD_HOC)
            .status(CycleStatus.OPEN)
            .build();

    Member existingMember = Member.builder().id(1L).build();
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(existingMember));
    when(menteeRepository.create(any(Mentee.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(menteeRepository.update(any(), any(Mentee.class)))
        .thenAnswer(invocation -> invocation.getArgument(1));
    when(menteeRepository.findById(any()))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(mentee));
    when(cycleRepository.findOpenCycle()).thenReturn(Optional.of(cycle));
    when(applicationRepository.findByMenteeAndCycle(any(), any())).thenReturn(List.of());

    Mentee result = menteeService.saveRegistration(registration);

    assertThat(result).isEqualTo(mentee);
    verify(cycleRepository, atLeastOnce()).findOpenCycle();
  }

  @Test
  @DisplayName(
      "Given mentee with existing id, when creating mentee, "
          + "then should use existing mentee without email lookup")
  void shouldReturnExistingMenteeWhenIdExistsInRepository() {
    var currentYear = Year.now();
    var existingMentee = createMenteeTest(5L, "Test Mentee", "test@wcc.com");
    var registration =
        new MenteeRegistration(
            existingMentee,
            MentorshipType.AD_HOC,
            currentYear,
            List.of(new MenteeApplicationDto(1L, 1, "msg", "why")));

    var cycle =
        MentorshipCycleEntity.builder()
            .cycleId(1L)
            .cycleYear(currentYear)
            .mentorshipType(MentorshipType.AD_HOC)
            .status(CycleStatus.OPEN)
            .build();

    when(cycleRepository.findOpenCycle()).thenReturn(Optional.of(cycle));
    when(menteeRepository.findById(5L)).thenReturn(Optional.of(existingMentee));
    when(applicationRepository.findByMenteeAndCycle(any(), any())).thenReturn(List.of());
    when(applicationRepository.countMenteeApplications(any(), any())).thenReturn(0L);
    when(menteeRepository.update(eq(5L), any(Mentee.class)))
        .thenAnswer(invocation -> invocation.getArgument(1));

    Mentee result = menteeService.saveRegistration(registration);

    assertThat(result).isEqualTo(existingMentee);
    verify(menteeRepository).update(eq(5L), any(Mentee.class));
    verify(memberRepository, never()).findByEmail(anyString());
  }

  @Test
  @DisplayName(
      "Given existing member who is a mentor, when registering as mentee, "
          + "then should preserve mentor type and add mentee type")
  void shouldPreserveMemberTypesWhenExistingMentorRegistersAsMentee() {
    var currentYear = Year.now();
    var menteeRequest = createMenteeTest(null, "Test Member", "mentor@wcc.com");
    var registration =
        new MenteeRegistration(
            menteeRequest,
            MentorshipType.AD_HOC,
            currentYear,
            List.of(new MenteeApplicationDto(1L, 1, "msg", "why")));

    var cycle =
        MentorshipCycleEntity.builder()
            .cycleId(1L)
            .cycleYear(currentYear)
            .mentorshipType(MentorshipType.AD_HOC)
            .status(CycleStatus.OPEN)
            .build();

    // Existing member who is already a MENTOR and MEMBER
    Member existingMentorMember =
        Member.builder()
            .id(100L)
            .email("mentor@wcc.com")
            .fullName("Test Member")
            .position("Senior Engineer")
            .slackDisplayName("testmember")
            .memberTypes(List.of(MemberType.MEMBER, MemberType.MENTOR))
            .build();

    when(memberRepository.findByEmail("mentor@wcc.com"))
        .thenReturn(Optional.of(existingMentorMember));
    when(memberRepository.findById(100L)).thenReturn(Optional.of(existingMentorMember));
    when(menteeRepository.findById(100L)).thenReturn(Optional.empty());
    when(cycleRepository.findOpenCycle()).thenReturn(Optional.of(cycle));
    when(applicationRepository.findByMenteeAndCycle(any(), any())).thenReturn(List.of());
    when(applicationRepository.countMenteeApplications(100L, 1L)).thenReturn(0L);
    // Capture the created mentee to verify member types
    Mentee[] capturedMentee = new Mentee[1];
    when(menteeRepository.create(any(Mentee.class)))
        .thenAnswer(
            invocation -> {
              capturedMentee[0] = invocation.getArgument(0);
              return capturedMentee[0];
            });
    when(menteeRepository.findById(100L))
        .thenReturn(Optional.empty())
        .thenAnswer(invocation -> Optional.ofNullable(capturedMentee[0]));

    Mentee result = menteeService.saveRegistration(registration);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(100L);
    assertThat(capturedMentee[0].getMemberTypes())
        .as("Member types should be preserved and merged")
        .containsExactlyInAnyOrder(MemberType.MEMBER, MemberType.MENTOR, MemberType.MENTEE);
    verify(memberRepository).findByEmail("mentor@wcc.com");
    verify(memberRepository).findById(100L);
    verify(menteeRepository).create(any(Mentee.class));
  }
}
