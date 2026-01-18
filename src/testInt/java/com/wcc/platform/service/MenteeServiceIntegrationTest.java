package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wcc.platform.domain.exceptions.MenteeRegistrationLimitException;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeApplicationDto;
import com.wcc.platform.domain.platform.mentorship.MenteeRegistration;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.factories.SetupMenteeFactories;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import java.time.Month;
import java.time.Year;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;

import static org.mockito.Mockito.when;

/**
 * Integration tests for MenteeService with PostgreSQL. Tests mentee registration with cycle year
 * validation.
 */
class MenteeServiceIntegrationTest extends DefaultDatabaseSetup {

  @Autowired private MenteeService menteeService;
  @Autowired private MenteeRepository menteeRepository;
  @Autowired private com.wcc.platform.repository.MentorRepository mentorRepository;
  @SpyBean private MentorshipConfig mentorshipConfig;
  @SpyBean private MentorshipConfig.Validation validation;
  @SpyBean private MentorshipService mentorshipService;

  private Mentee createdMentee;
  private Long testMentorId;

  @BeforeEach
  void setupTestData() {
    // Create a test mentor for applications to reference with unique email
    String uniqueEmail = "test-mentor-" + System.currentTimeMillis() + "@test.com";
    var testMentor = com.wcc.platform.factories.SetupMentorFactories.createMentorTest(
        null, "Test Mentor", uniqueEmail);
    var createdMentor = mentorRepository.create(testMentor);
    testMentorId = createdMentor.getId();
  }

  @AfterEach
  void cleanup() {
    if (createdMentee != null && createdMentee.getId() != null) {
      menteeRepository.deleteById(createdMentee.getId());
    }
    if (testMentorId != null) {
      mentorRepository.deleteById(testMentorId);
    }
  }

  @Test
  @DisplayName("Given valid mentee registration, when saving, then it should succeed")
  void shouldSaveRegistrationMentee() {
    final Mentee mentee =
        SetupMenteeFactories.createMenteeTest(
            null, "Integration Test Mentee", "integration-mentee@test.com");

    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.LONG_TERM,
            Year.of(2026),
            List.of(new MenteeApplicationDto(null, testMentorId, 1)));

    createdMentee = menteeService.saveRegistration(registration);

    assertThat(createdMentee).isNotNull();
    assertThat(createdMentee.getId()).isNotNull();
    assertThat(createdMentee.getFullName()).isEqualTo("Integration Test Mentee");
    assertThat(createdMentee.getEmail()).isEqualTo("integration-mentee@test.com");
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
            List.of(new MenteeApplicationDto(null, testMentorId, 1)));

    createdMentee = menteeService.saveRegistration(registration);

    assertThat(createdMentee).isNotNull();
    assertThat(createdMentee.getId()).isNotNull();
  }

  @Test
  @DisplayName(
      "Given ad-hoc mentorship type when cycle type is long-term, then it should throw InvalidMentorshipTypeException")
  void shouldThrowExceptionWhenMentorshipTypeDoesNotMatch() {
    final Mentee mentee =
        SetupMenteeFactories.createMenteeTest(null, "Ad Hoc Mentee", "adhoc-mentee@test.com");

    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.AD_HOC,
            Year.of(2026),
            List.of(new MenteeApplicationDto(null, testMentorId, 1)));

    assertThatThrownBy(() -> menteeService.saveRegistration(registration))
        .isInstanceOf(InvalidMentorshipTypeException.class)
        .hasMessageContaining("does not match current cycle type");
  }

  @Test
  @DisplayName("Given mentee exceeds 5 applications, when registering, then it should throw")
  void shouldThrowExceptionWhenRegistrationLimitExceeded() {
    final Mentee mentee =
        SetupMenteeFactories.createMenteeTest(null, "Limit Test", "limit-test@test.com");

    MenteeRegistration initialRegistration =
        new MenteeRegistration(
            mentee,
            MentorshipType.LONG_TERM,
            Year.of(2026),
            List.of(
                new MenteeApplicationDto(null, testMentorId, 1),
                new MenteeApplicationDto(null, testMentorId, 2),
                new MenteeApplicationDto(null, testMentorId, 3),
                new MenteeApplicationDto(null, testMentorId, 4),
                new MenteeApplicationDto(null, testMentorId, 5)));

    createdMentee = menteeService.saveRegistration(initialRegistration);
    assertThat(createdMentee.getId()).isNotNull();

    final Mentee menteeWithId =
        Mentee.menteeBuilder()
            .id(createdMentee.getId())
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

    MenteeRegistration exceedingRegistration =
        new MenteeRegistration(
            menteeWithId,
            MentorshipType.LONG_TERM,
            Year.of(2026),
            List.of(new MenteeApplicationDto(menteeWithId.getId(), testMentorId, 1)));

    assertThatThrownBy(() -> menteeService.saveRegistration(exceedingRegistration))
        .isInstanceOf(MenteeRegistrationLimitExceededException.class);
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
            List.of(new MenteeApplicationDto(null, testMentorId, 1)));

    createdMentee = menteeService.saveRegistration(registration);

    final var allMentees = menteeService.getAllMentees();

    assertThat(allMentees).isNotEmpty();
    assertThat(allMentees).anyMatch(m -> m.getId().equals(createdMentee.getId()));
  }

  @Test
  @DirtiesContext
  @DisplayName(
      "Given validation enabled and cycle is closed, when registering, then it should throw MentorshipCycleClosedException")
  void shouldThrowExceptionWhenValidationEnabledAndCycleIsClosed() {
    when(validation.isEnabled()).thenReturn(true);
    when(mentorshipConfig.getValidation()).thenReturn(validation);
    when(mentorshipService.getCurrentCycle()).thenReturn(MentorshipService.CYCLE_CLOSED);

    final Mentee mentee =
        SetupMenteeFactories.createMenteeTest(null, "Closed Cycle Test", "closed@test.com");

    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.LONG_TERM,
            Year.of(2026),
            List.of(new MenteeApplicationDto(null, testMentorId, 1)));

    assertThatThrownBy(() -> menteeService.saveRegistration(registration))
        .isInstanceOf(MentorshipCycleClosedException.class)
        .hasMessageContaining("Mentorship cycle");
  }

  @Test
  @DirtiesContext
  @DisplayName(
      "Given validation disabled, when registering with current year, then it should succeed")
  void shouldSucceedWhenValidationDisabled() {
    when(validation.isEnabled()).thenReturn(false);
    when(mentorshipConfig.getValidation()).thenReturn(validation);

    final Mentee mentee =
        SetupMenteeFactories.createMenteeTest(
            null, "Validation Disabled Test", "validation-disabled@test.com");

    // Use 2026 which exists in database from V18 migration
    MenteeRegistration registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.LONG_TERM,
            Year.of(2026),
            List.of(new MenteeApplicationDto(null, testMentorId, 1)));

    createdMentee = menteeService.saveRegistration(registration);

    assertThat(createdMentee).isNotNull();
    assertThat(createdMentee.getId()).isNotNull();
  }
}
