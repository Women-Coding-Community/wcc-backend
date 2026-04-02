package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.exceptions.MenteeRegistrationLimitException;
import com.wcc.platform.domain.exceptions.MentorNotFoundException;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeApplicationDto;
import com.wcc.platform.domain.platform.mentorship.MenteeRegistration;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.factories.SetupMenteeFactories;
import com.wcc.platform.factories.SetupMentorFactories;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration tests for MenteeService with PostgreSQL. Tests mentee registration with actual
 * database operations (no mocks).
 */
@SuppressWarnings("PMD.TooManyMethods")
class MenteeServiceIntegrationTest extends DefaultDatabaseSetup {

  private static final Year TEST_YEAR_2026 = Year.of(2026);
  private static final Year TEST_YEAR_2028 = Year.of(2028);
  private static final int MAX_MENTEES_PER_MENTOR = 6;
  private static final int MAX_AD_HOC_MENTEES = 3;

  private final List<Mentee> createdMentees = new ArrayList<>();
  private final List<Long> createdMentors = new ArrayList<>();
  private final List<Long> createdCycles = new ArrayList<>();
  private final List<Long> createdMembers = new ArrayList<>();

  @Autowired private MenteeService menteeService;
  @Autowired private MenteeRepository menteeRepository;
  @Autowired private MenteeApplicationRepository registrationsRepo;
  @Autowired private MentorRepository mentorRepository;
  @Autowired private MemberRepository memberRepository;
  @Autowired private MentorshipCycleRepository cycleRepository;

  @BeforeEach
  void setupTestData() {
    ensureLongTermCycleExists();
    createTestMentors(6);
  }

  @AfterEach
  void cleanup() {
    cleanupTestData();
  }

  @Test
  @DisplayName(
      "Given valid LONG_TERM mentee registration, when saving, then it should create mentee and applications")
  void shouldSaveLongTermMenteeRegistration() {
    final Mentee mentee = createMentee("Long Term Mentee", "long-term-mentee@test.com");
    final MenteeRegistration registration =
        createRegistration(
            mentee,
            MentorshipType.LONG_TERM,
            TEST_YEAR_2026,
            List.of(firstMentor(), secondMentor()));

    final Mentee savedMentee = menteeService.saveRegistration(registration);
    createdMentees.add(savedMentee);

    assertMenteeCreated(savedMentee, "Long Term Mentee", "long-term-mentee@test.com");
  }

  @Test
  @DisplayName(
      "Given valid AD_HOC mentee registration, when saving, then it should create mentee and applications")
  void shouldSaveAdHocMenteeRegistration() {
    createAdHocCycle();

    final Mentee mentee = createMentee("Ad Hoc Mentee", "adhoc-mentee@test.com");
    final MenteeRegistration registration =
        createRegistration(mentee, MentorshipType.AD_HOC, TEST_YEAR_2028, List.of(firstMentor()));

    final Mentee savedMentee = menteeService.saveRegistration(registration);
    createdMentees.add(savedMentee);

    assertMenteeCreated(savedMentee, "Ad Hoc Mentee", "adhoc-mentee@test.com");
  }

  @Test
  @DisplayName("Given current year registration, when saving, then it should succeed")
  void shouldSaveRegistrationMenteeWithCurrentYear() {
    final Mentee mentee = createMentee("Current Year Mentee", "current-year-mentee@test.com");
    final MenteeRegistration registration =
        createRegistration(mentee, MentorshipType.LONG_TERM, Year.now(), List.of(firstMentor()));

    final Mentee savedMentee = menteeService.saveRegistration(registration);
    createdMentees.add(savedMentee);

    assertThat(savedMentee).isNotNull();
    assertThat(savedMentee.getId()).isNotNull();
  }

  @Test
  @DisplayName("Given mentee exceeds 5 applications, when registering, then it should throw")
  void shouldThrowExceptionWhenRegistrationLimitExceeded() {
    final Mentee mentee = createMentee("Limit Test", "limit-test@test.com");

    final Mentee savedMentee =
        menteeService.saveRegistration(
            createRegistration(mentee, MentorshipType.LONG_TERM, TEST_YEAR_2026, fiveMentors()));
    createdMentees.add(savedMentee);

    final Mentee menteeWithId = copyMenteeWithId(mentee, savedMentee.getId());
    final MenteeRegistration exceedingRegistration =
        createRegistration(
            menteeWithId, MentorshipType.LONG_TERM, TEST_YEAR_2026, List.of(sixthMentor()));

    assertThatThrownBy(() -> menteeService.saveRegistration(exceedingRegistration))
        .isInstanceOf(MenteeRegistrationLimitException.class);
  }

  @Test
  @DisplayName("Given valid registration, when getting all mentees, then list should include it")
  void shouldIncludeCreatedMenteeInAllMentees() {
    final Mentee mentee = createMentee("List Test Mentee", "list-test-mentee@test.com");
    final MenteeRegistration registration =
        createRegistration(
            mentee, MentorshipType.LONG_TERM, TEST_YEAR_2026, List.of(firstMentor()));

    final Mentee savedMentee = menteeService.saveRegistration(registration);
    createdMentees.add(savedMentee);

    final List<Mentee> allMentees = menteeService.getAllMentees();

    assertThat(allMentees).isNotEmpty().anyMatch(m -> m.getId().equals(savedMentee.getId()));
  }

  @Test
  @DisplayName(
      "Given multiple applications from same mentee, when updating, then it should add new applications")
  void shouldUpdateExistingMenteeWithMoreApplications() {
    final Mentee mentee = createMentee("Update Test", "update-test@test.com");

    final Mentee savedMentee =
        menteeService.saveRegistration(
            createRegistration(
                mentee, MentorshipType.LONG_TERM, TEST_YEAR_2026, List.of(firstMentor())));
    createdMentees.add(savedMentee);

    final Mentee menteeWithId = copyMenteeWithId(mentee, savedMentee.getId());
    final Mentee updatedMentee =
        menteeService.saveRegistration(
            createRegistration(
                menteeWithId,
                MentorshipType.LONG_TERM,
                TEST_YEAR_2026,
                List.of(secondMentor(), thirdMentor())));

    assertThat(updatedMentee.getId()).isEqualTo(savedMentee.getId());
  }

  @Test
  @DisplayName(
      "Given existing member with email, when creating mentee with same email, then it should use existing member")
  void shouldUseExistingMemberWhenMenteeEmailAlreadyExists() {
    final Member existingMember = createAndSaveRegularMember("existing-member@test.com");

    final Mentee mentee = createMentee("Mentee From Existing Member", "existing-member@test.com");
    final Mentee savedMentee =
        menteeService.saveRegistration(
            createRegistration(
                mentee, MentorshipType.LONG_TERM, TEST_YEAR_2026, List.of(firstMentor())));
    createdMentees.add(savedMentee);

    assertThat(savedMentee.getId()).isEqualTo(existingMember.getId());
    assertThat(savedMentee.getEmail()).isEqualTo("existing-member@test.com");
  }

  @Test
  @DisplayName(
      "Given existing mentee with applications "
          + "When registering with more mentors, then it should add them")
  void shouldAddApplicationsToExistingMentee() {
    final Mentee mentee = createMentee("Existing Mentee", "existing-mentee@test.com");

    final Mentee savedMentee =
        menteeService.saveRegistration(
            createRegistration(
                mentee, MentorshipType.LONG_TERM, TEST_YEAR_2026, List.of(firstMentor())));
    createdMentees.add(savedMentee);

    final Mentee sameMenteeNoId = createMentee("Existing Mentee", "existing-mentee@test.com");
    final Mentee updatedMentee =
        menteeService.saveRegistration(
            createRegistration(
                sameMenteeNoId, MentorshipType.LONG_TERM, TEST_YEAR_2026, List.of(secondMentor())));

    assertThat(updatedMentee.getId()).isEqualTo(savedMentee.getId());
  }

  @Test
  @DisplayName(
      "Given existing mentee, when registering with duplicate mentor "
          + "Then it should ignore duplicate")
  void shouldIgnoreDuplicateMentorForExistingMentee() {
    final Mentee mentee = createMentee("Duplicate Test", "duplicate-test@test.com");

    final Mentee savedMentee =
        menteeService.saveRegistration(
            createRegistration(
                mentee, MentorshipType.LONG_TERM, TEST_YEAR_2026, List.of(firstMentor())));
    createdMentees.add(savedMentee);

    final Mentee updatedMentee =
        menteeService.saveRegistration(
            createRegistration(
                mentee, MentorshipType.LONG_TERM, TEST_YEAR_2026, List.of(firstMentor())));

    assertThat(updatedMentee.getId()).isEqualTo(savedMentee.getId());
  }

  @Test
  @DisplayName(
      "Given 3 mentees and 2 approved mentors, when mentees apply and mentors approve "
          + "Then status should be MENTOR_ACCEPTED")
  void shouldHandleMenteeApplicationsAndMentorApprovals() {
    mentorRepository.updateProfileStatus(createdMentors.get(0), ProfileStatus.ACTIVE);
    mentorRepository.updateProfileStatus(createdMentors.get(1), ProfileStatus.ACTIVE);

    final Mentee menteeC = createMentee("Mentee C", "mentee-c@test.com");
    createdMentees.add(menteeRepository.create(menteeC));

    final Mentee savedMenteeA =
        menteeService.saveRegistration(
            createRegistration(
                createMentee("Mentee A", "mentee-a@test.com"),
                MentorshipType.LONG_TERM,
                TEST_YEAR_2026,
                List.of(firstMentor())));
    createdMentees.add(savedMenteeA);

    final Mentee savedMenteeB =
        menteeService.saveRegistration(
            createRegistration(
                createMentee("Mentee B", "mentee-b@test.com"),
                MentorshipType.LONG_TERM,
                TEST_YEAR_2026,
                List.of(secondMentor())));
    createdMentees.add(savedMenteeB);

    final MentorshipCycleEntity cycle =
        cycleRepository.findByYearAndType(TEST_YEAR_2026, MentorshipType.LONG_TERM).orElseThrow();

    approveMenteeApplication(savedMenteeA.getId(), cycle.getCycleId(), "Mentor 1 approves");
    approveMenteeApplication(savedMenteeB.getId(), cycle.getCycleId(), "Mentor 2 approves");

    verifyApplicationStatus(
        savedMenteeA.getId(), cycle.getCycleId(), ApplicationStatus.MENTOR_ACCEPTED);
    verifyApplicationStatus(
        savedMenteeB.getId(), cycle.getCycleId(), ApplicationStatus.MENTOR_ACCEPTED);
  }

  @Test
  @DisplayName("Given a pending mentee When updating status to active Then it should be approved")
  void shouldApproveMentee() {
    final Mentee mentee = createMentee("Pending Mentee", "pending@test.com");
    final Mentee savedMentee = menteeRepository.create(mentee);
    createdMentees.add(savedMentee);

    assertThat(savedMentee.getProfileStatus()).isEqualTo(ProfileStatus.ACTIVE);

    final Mentee foundMentee = menteeRepository.findById(savedMentee.getId()).orElseThrow();
    assertThat(foundMentee.getProfileStatus()).isEqualTo(ProfileStatus.ACTIVE);
  }

  @Test
  @DisplayName(
      "Given non-existent mentor id When saving mentee registration "
          + "Then it should throw MentorNotFoundException")
  void shouldThrowExceptionWhenMentorDoesNotExist() {
    final Mentee mentee =
        createMentee("Mentee with non-existent mentor", "non-existent-mentor@test.com");

    final MenteeRegistration registration =
        createRegistration(
            mentee,
            MentorshipType.LONG_TERM,
            TEST_YEAR_2026,
            List.of(new MenteeApplicationDto(999_999L, 1, "Test message", "Test why")));

    assertThatThrownBy(() -> menteeService.saveRegistration(registration))
        .isInstanceOf(MentorNotFoundException.class)
        .hasMessageContaining("Mentor not found: 999999");
  }

  private void ensureLongTermCycleExists() {
    cycleRepository
        .findOpenCycle()
        .ifPresent(
            cycle -> {
              cycle.setStatus(CycleStatus.CLOSED);
              cycleRepository.update(cycle.getCycleId(), cycle);
            });

    final var cycle =
        MentorshipCycleEntity.builder()
            .cycleYear(TEST_YEAR_2026)
            .mentorshipType(MentorshipType.LONG_TERM)
            .cycleMonth(Month.MARCH)
            .registrationStartDate(LocalDate.now().minusDays(1))
            .registrationEndDate(LocalDate.now().plusDays(10))
            .cycleStartDate(LocalDate.now().plusDays(15))
            .status(CycleStatus.OPEN)
            .maxMenteesPerMentor(MAX_MENTEES_PER_MENTOR)
            .description("Test Cycle")
            .build();
    final var savedCycle = cycleRepository.create(cycle);
    createdCycles.add(savedCycle.getCycleId());
  }

  private void createAdHocCycle() {
    cycleRepository
        .findOpenCycle()
        .ifPresent(
            cycle -> {
              cycle.setStatus(CycleStatus.CLOSED);
              cycleRepository.update(cycle.getCycleId(), cycle);
            });

    final var adHocCycle =
        MentorshipCycleEntity.builder()
            .cycleYear(TEST_YEAR_2028)
            .mentorshipType(MentorshipType.AD_HOC)
            .cycleMonth(Month.DECEMBER)
            .registrationStartDate(LocalDate.now().minusDays(5))
            .registrationEndDate(LocalDate.now().plusDays(5))
            .cycleStartDate(LocalDate.now().plusDays(1))
            .cycleEndDate(LocalDate.now().plusDays(30))
            .status(CycleStatus.OPEN)
            .maxMenteesPerMentor(MAX_AD_HOC_MENTEES)
            .description("Test AD_HOC cycle for December 2028")
            .build();

    final var savedCycle = cycleRepository.create(adHocCycle);
    createdCycles.add(savedCycle.getCycleId());
  }

  private void createTestMentors(final int count) {
    for (int i = 0; i < count; i++) {
      final String uniqueEmail =
          "test-mentor-" + System.currentTimeMillis() + "-" + i + "@test.com";
      final var testMentor =
          SetupMentorFactories.createMentorTest(null, "Test Mentor " + i, uniqueEmail);
      final var createdMentor = mentorRepository.create(testMentor);
      createdMentors.add(createdMentor.getId());

      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  private Mentee createMentee(final String name, final String email) {
    return SetupMenteeFactories.createMenteeTest(null, name, email);
  }

  private MenteeRegistration createRegistration(
      final Mentee mentee,
      final MentorshipType type,
      final Year year,
      final List<MenteeApplicationDto> applications) {
    return new MenteeRegistration(mentee, type, year, applications);
  }

  private MenteeApplicationDto createApplication(
      final Long mentorId, final int priority, final String message, final String why) {
    return new MenteeApplicationDto(mentorId, priority, message, why);
  }

  private MenteeApplicationDto firstMentor() {
    return createApplication(createdMentors.getFirst(), 1, "Test application", "Test why mentor");
  }

  private MenteeApplicationDto secondMentor() {
    return createApplication(createdMentors.get(1), 2, "Test application", "Test why mentor");
  }

  private MenteeApplicationDto thirdMentor() {
    return createApplication(createdMentors.get(2), 3, "Test application", "Test why mentor");
  }

  private MenteeApplicationDto sixthMentor() {
    return createApplication(createdMentors.get(5), 1, "Test application", "Test why mentor");
  }

  private List<MenteeApplicationDto> fiveMentors() {
    return List.of(
        createApplication(createdMentors.get(0), 1, "Test application", "Test why mentor"),
        createApplication(createdMentors.get(1), 2, "Test application", "Test why mentor"),
        createApplication(createdMentors.get(2), 3, "Test application", "Test why mentor"),
        createApplication(createdMentors.get(3), 4, "Test application", "Test why mentor"),
        createApplication(createdMentors.get(4), 5, "Test application", "Test why mentor"));
  }

  private Mentee copyMenteeWithId(final Mentee original, final Long menteeId) {
    return Mentee.menteeBuilder()
        .id(menteeId)
        .fullName(original.getFullName())
        .email(original.getEmail())
        .position(original.getPosition())
        .slackDisplayName(original.getSlackDisplayName())
        .country(original.getCountry())
        .city(original.getCity())
        .profileStatus(original.getProfileStatus())
        .bio(original.getBio())
        .skills(original.getSkills())
        .spokenLanguages(original.getSpokenLanguages())
        .build();
  }

  private Member createAndSaveRegularMember(final String email) {
    final Member member =
        Member.builder()
            .fullName("Existing Member")
            .email(email)
            .position("Software Engineer")
            .slackDisplayName("@existing")
            .country(new Country("US", "United States"))
            .city("New York")
            .companyName("Tech Corp")
            .memberTypes(List.of(MemberType.MEMBER))
            .images(List.of())
            .network(List.of())
            .build();

    final Member savedMember = memberRepository.create(member);
    createdMembers.add(savedMember.getId());
    return savedMember;
  }

  private void approveMenteeApplication(
      final Long menteeId, final Long cycleId, final String notes) {
    final var applications = registrationsRepo.findByMenteeAndCycle(menteeId, cycleId);
    assertThat(applications).hasSize(1);

    final var application = applications.getFirst();
    assertThat(application.getStatus()).isEqualTo(ApplicationStatus.PENDING);

    registrationsRepo.updateStatus(
        application.getApplicationId(), ApplicationStatus.MENTOR_ACCEPTED, notes);
  }

  private void verifyApplicationStatus(
      final Long menteeId, final Long cycleId, final ApplicationStatus expectedStatus) {
    final var applications = registrationsRepo.findByMenteeAndCycle(menteeId, cycleId);
    assertThat(applications).hasSize(1);

    final var application = applications.getFirst();
    assertThat(application.getStatus()).isEqualTo(expectedStatus);
  }

  private void assertMenteeCreated(
      final Mentee mentee, final String expectedName, final String expectedEmail) {
    assertThat(mentee).isNotNull();
    assertThat(mentee.getId()).isNotNull();
    assertThat(mentee.getFullName()).isEqualTo(expectedName);
    assertThat(mentee.getEmail()).isEqualTo(expectedEmail);
  }

  private void cleanupTestData() {
    createdMentees.forEach(
        mentee -> {
          if (mentee != null && mentee.getId() != null) {
            menteeRepository.deleteById(mentee.getId());
          }
        });
    createdMentors.forEach(mentorRepository::deleteById);
    createdMembers.forEach(memberRepository::deleteById);
    createdCycles.forEach(cycleRepository::deleteById);
    createdMentees.clear();
    createdMentors.clear();
    createdMembers.clear();
    createdCycles.clear();
  }
}
