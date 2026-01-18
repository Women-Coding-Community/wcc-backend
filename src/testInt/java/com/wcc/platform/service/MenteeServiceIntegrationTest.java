package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wcc.platform.domain.exceptions.MenteeRegistrationLimitException;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeApplicationDto;
import com.wcc.platform.domain.platform.mentorship.MenteeRegistration;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.factories.SetupMenteeFactories;
import com.wcc.platform.repository.MenteeRepository;
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
class MenteeServiceIntegrationTest extends DefaultDatabaseSetup {

  private final List<Mentee> createdMentees = new ArrayList<>();
  private final List<Long> createdMentors = new ArrayList<>();
  private final List<Long> createdCycles = new ArrayList<>();

  @Autowired private MenteeService menteeService;
  @Autowired private MenteeRepository menteeRepository;
  @Autowired private com.wcc.platform.repository.MentorRepository mentorRepository;
  @Autowired private MentorshipCycleRepository cycleRepository;

  @BeforeEach
  void setupTestData() {

    var cycle = cycleRepository.findByYearAndType(Year.of(2026), MentorshipType.LONG_TERM);
    if (cycle.isEmpty()) {
      cycleRepository.create(
          MentorshipCycleEntity.builder()
              .cycleYear(Year.of(2026))
              .mentorshipType(MentorshipType.LONG_TERM)
              .cycleMonth(Month.MARCH)
              .registrationStartDate(LocalDate.now().minusDays(1))
              .registrationEndDate(LocalDate.now().plusDays(10))
              .cycleStartDate(LocalDate.now().plusDays(15))
              .status(CycleStatus.OPEN)
              .maxMenteesPerMentor(6)
              .description("Test Cycle")
              .build());
    }

    // Create test mentors for applications to reference
    for (int i = 0; i < 6; i++) {
      String uniqueEmail = "test-mentor-" + System.currentTimeMillis() + "-" + i + "@test.com";
      var testMentor =
          com.wcc.platform.factories.SetupMentorFactories.createMentorTest(
              null, "Test Mentor " + i, uniqueEmail);
      var createdMentor = mentorRepository.create(testMentor);
      createdMentors.add(createdMentor.getId());

      // Small delay to ensure unique timestamps
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  @AfterEach
  void cleanup() {
    // Clean up in reverse order to respect foreign keys
    createdMentees.forEach(
        mentee -> {
          if (mentee != null && mentee.getId() != null) {
            menteeRepository.deleteById(mentee.getId());
          }
        });
    createdMentors.forEach(mentorRepository::deleteById);
    createdCycles.forEach(cycleRepository::deleteById);
    createdMentees.clear();
    createdMentors.clear();
    createdCycles.clear();
  }

  @Test
  @DisplayName(
      "Given valid LONG_TERM mentee registration, when saving, then it should create mentee and applications")
  void shouldSaveLongTermMenteeRegistration() {
    final Mentee mentee =
        SetupMenteeFactories.createMenteeTest(
            null, "Long Term Mentee", "long-term-mentee@test.com");

    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.LONG_TERM,
            Year.of(2026),
            List.of(
                new MenteeApplicationDto(null, createdMentors.getFirst(), 1),
                new MenteeApplicationDto(null, createdMentors.get(1), 2)));

    var savedMentee = menteeService.saveRegistration(registration);
    createdMentees.add(savedMentee);

    assertThat(savedMentee).isNotNull();
    assertThat(savedMentee.getId()).isNotNull();
    assertThat(savedMentee.getFullName()).isEqualTo("Long Term Mentee");
    assertThat(savedMentee.getEmail()).isEqualTo("long-term-mentee@test.com");
  }

  @Test
  @DisplayName(
      "Given valid AD_HOC mentee registration, when saving, then it should create mentee and applications")
  void shouldSaveAdHocMenteeRegistration() {
    // Create an AD_HOC cycle for December 2028 (well into the future)
    var adHocCycle =
        MentorshipCycleEntity.builder()
            .cycleYear(Year.of(2028))
            .mentorshipType(MentorshipType.AD_HOC)
            .cycleMonth(Month.DECEMBER)
            .registrationStartDate(LocalDate.now().minusDays(5))
            .registrationEndDate(LocalDate.now().plusDays(5))
            .cycleStartDate(LocalDate.now().plusDays(1))
            .cycleEndDate(LocalDate.now().plusDays(30))
            .status(CycleStatus.OPEN)
            .maxMenteesPerMentor(3)
            .description("Test AD_HOC cycle for December 2028")
            .build();

    var savedCycle = cycleRepository.create(adHocCycle);
    createdCycles.add(savedCycle.getCycleId());

    final Mentee mentee =
        SetupMenteeFactories.createMenteeTest(null, "Ad Hoc Mentee", "adhoc-mentee@test.com");

    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.AD_HOC,
            Year.of(2028),
            List.of(new MenteeApplicationDto(null, createdMentors.getFirst(), 1)));

    var savedMentee = menteeService.saveRegistration(registration);
    createdMentees.add(savedMentee);

    assertThat(savedMentee).isNotNull();
    assertThat(savedMentee.getId()).isNotNull();
    assertThat(savedMentee.getFullName()).isEqualTo("Ad Hoc Mentee");
    assertThat(savedMentee.getEmail()).isEqualTo("adhoc-mentee@test.com");
  }

  @Test
  @DisplayName("Given current year registration, when saving, then it should succeed")
  void shouldSaveRegistrationMenteeWithCurrentYear() {
    final Mentee mentee =
        SetupMenteeFactories.createMenteeTest(
            null, "Current Year Mentee", "current-year-mentee@test.com");

    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.LONG_TERM,
            Year.now(),
            List.of(new MenteeApplicationDto(null, createdMentors.getFirst(), 1)));

    var savedMentee = menteeService.saveRegistration(registration);
    createdMentees.add(savedMentee);

    assertThat(savedMentee).isNotNull();
    assertThat(savedMentee.getId()).isNotNull();
  }

  @Test
  @DisplayName("Given mentee exceeds 5 applications, when registering, then it should throw")
  void shouldThrowExceptionWhenRegistrationLimitExceeded() {
    final Mentee mentee =
        SetupMenteeFactories.createMenteeTest(null, "Limit Test", "limit-test@test.com");

    // Create initial registration with 5 applications to 5 different mentors
    MenteeRegistration initialRegistration =
        new MenteeRegistration(
            mentee,
            MentorshipType.LONG_TERM,
            Year.of(2026),
            List.of(
                new MenteeApplicationDto(null, createdMentors.getFirst(), 1),
                new MenteeApplicationDto(null, createdMentors.get(1), 2),
                new MenteeApplicationDto(null, createdMentors.get(2), 3),
                new MenteeApplicationDto(null, createdMentors.get(3), 4),
                new MenteeApplicationDto(null, createdMentors.get(4), 5)));

    var savedMentee = menteeService.saveRegistration(initialRegistration);
    createdMentees.add(savedMentee);
    assertThat(savedMentee.getId()).isNotNull();

    // Create mentee object with ID for update
    final Mentee menteeWithId =
        Mentee.menteeBuilder()
            .id(savedMentee.getId())
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

    // Try to add a 6th application - should fail
    MenteeRegistration exceedingRegistration =
        new MenteeRegistration(
            menteeWithId,
            MentorshipType.LONG_TERM,
            Year.of(2026),
            List.of(new MenteeApplicationDto(menteeWithId.getId(), createdMentors.get(5), 1)));

    assertThatThrownBy(() -> menteeService.saveRegistration(exceedingRegistration))
        .isInstanceOf(MenteeRegistrationLimitException.class);
  }

  @Test
  @DisplayName("Given valid registration, when getting all mentees, then list should include it")
  void shouldIncludeCreatedMenteeInAllMentees() {
    final Mentee mentee =
        SetupMenteeFactories.createMenteeTest(
            null, "List Test Mentee", "list-test-mentee@test.com");

    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.LONG_TERM,
            Year.of(2026),
            List.of(new MenteeApplicationDto(null, createdMentors.getFirst(), 1)));

    var savedMentee = menteeService.saveRegistration(registration);
    createdMentees.add(savedMentee);

    final var allMentees = menteeService.getAllMentees();

    assertThat(allMentees).isNotEmpty();
    assertThat(allMentees).anyMatch(m -> m.getId().equals(savedMentee.getId()));
  }

  @Test
  @DisplayName(
      "Given multiple applications from same mentee, when updating, then it should add new applications")
  void shouldUpdateExistingMenteeWithMoreApplications() {
    final Mentee mentee =
        SetupMenteeFactories.createMenteeTest(null, "Update Test", "update-test@test.com");

    // Initial registration with 1 application
    MenteeRegistration initialRegistration =
        new MenteeRegistration(
            mentee,
            MentorshipType.LONG_TERM,
            Year.of(2026),
            List.of(new MenteeApplicationDto(null, createdMentors.getFirst(), 1)));

    var savedMentee = menteeService.saveRegistration(initialRegistration);
    createdMentees.add(savedMentee);
    assertThat(savedMentee.getId()).isNotNull();

    // Create mentee object with ID for second registration
    final Mentee menteeWithId =
        Mentee.menteeBuilder()
            .id(savedMentee.getId())
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

    // Second registration with 2 more applications (total 3)
    MenteeRegistration secondRegistration =
        new MenteeRegistration(
            menteeWithId,
            MentorshipType.LONG_TERM,
            Year.of(2026),
            List.of(
                new MenteeApplicationDto(menteeWithId.getId(), createdMentors.get(1), 2),
                new MenteeApplicationDto(menteeWithId.getId(), createdMentors.get(2), 3)));

    var updatedMentee = menteeService.saveRegistration(secondRegistration);

    assertThat(updatedMentee).isNotNull();
    assertThat(updatedMentee.getId()).isEqualTo(savedMentee.getId());
  }
}
