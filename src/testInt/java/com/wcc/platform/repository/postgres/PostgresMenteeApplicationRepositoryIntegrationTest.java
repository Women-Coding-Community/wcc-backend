package com.wcc.platform.repository.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.factories.SetupMenteeFactories;
import com.wcc.platform.factories.SetupMentorFactories;
import com.wcc.platform.repository.postgres.mentorship.PostgresMenteeApplicationRepository;
import com.wcc.platform.repository.postgres.mentorship.PostgresMenteeRepository;
import com.wcc.platform.repository.postgres.mentorship.PostgresMentorRepository;
import com.wcc.platform.repository.postgres.mentorship.PostgresMentorshipCycleRepository;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Integration tests for PostgresMenteeApplicationRepository. */
class PostgresMenteeApplicationRepositoryIntegrationTest extends DefaultDatabaseSetup {

  @Autowired private PostgresMenteeApplicationRepository applicationRepository;
  @Autowired private PostgresMenteeRepository menteeRepository;
  @Autowired private PostgresMentorRepository mentorRepository;
  @Autowired private PostgresMentorshipCycleRepository cycleRepository;
  @Autowired private PostgresMemberRepository memberRepository;

  private Mentee mentee;
  private Mentor mentor;
  private MentorshipCycleEntity cycle;

  @BeforeEach
  void setUp() {
    // Clean up before starting
    memberRepository.deleteByEmail("mentor_app@test.com");
    memberRepository.deleteByEmail("mentee_app@test.com");
    cycleRepository
        .findByYearAndType(Year.of(2026), MentorshipType.LONG_TERM)
        .ifPresent(c -> cycleRepository.deleteById(c.getCycleId()));

    // Setup cycle
    cycle =
        cycleRepository.create(
            MentorshipCycleEntity.builder()
                .cycleYear(Year.of(2026))
                .mentorshipType(MentorshipType.LONG_TERM)
                .cycleMonth(Month.JANUARY)
                .registrationStartDate(LocalDate.now().minusDays(1))
                .registrationEndDate(LocalDate.now().plusDays(10))
                .cycleStartDate(LocalDate.now().plusDays(15))
                .status(CycleStatus.OPEN)
                .maxMenteesPerMentor(3)
                .description("Test Cycle")
                .build());

    // Setup mentor
    mentor =
        mentorRepository.create(
            SetupMentorFactories.createMentorTest(null, "Mentor App", "mentor_app@test.com"));

    // Setup mentee
    mentee =
        menteeRepository.create(
            SetupMenteeFactories.createMenteeTest(null, "Mentee App", "mentee_app@test.com"));
  }

  @AfterEach
  void tearDown() {
    // Applications will be deleted via CASCADE when mentee/mentor/cycle is deleted
    if (mentee != null) {
      menteeRepository.deleteById(mentee.getId());
      memberRepository.deleteById(mentee.getId());
    }
    if (mentor != null) {
      mentorRepository.deleteById(mentor.getId());
      memberRepository.deleteById(mentor.getId());
    }
    if (cycle != null) {
      cycleRepository.deleteById(cycle.getCycleId());
    }
  }

  @Test
  @DisplayName("Given valid application data, when creating application, then it should be saved")
  void shouldCreateApplication() {
    MenteeApplication application =
        MenteeApplication.builder()
            .menteeId(mentee.getId())
            .mentorId(mentor.getId())
            .cycleId(cycle.getCycleId())
            .priorityOrder(1)
            .status(ApplicationStatus.PENDING)
            .applicationMessage("I want to learn")
            .build();

    MenteeApplication created = applicationRepository.create(application);

    assertNotNull(created.getApplicationId());
    assertEquals(mentee.getId(), created.getMenteeId());
    assertEquals(mentor.getId(), created.getMentorId());
    assertEquals(cycle.getCycleId(), created.getCycleId());
    assertEquals(ApplicationStatus.PENDING, created.getStatus());
    assertEquals("I want to learn", created.getApplicationMessage());
    assertNotNull(created.getAppliedAt());
  }

  @Test
  @DisplayName("Given existing application, when finding by ID, then it should return application")
  void shouldFindById() {
    MenteeApplication created = createTestApplication(1);

    Optional<MenteeApplication> found = applicationRepository.findById(created.getApplicationId());

    assertTrue(found.isPresent());
    assertEquals(created.getApplicationId(), found.get().getApplicationId());
  }

  @Test
  @DisplayName(
      "Given existing application, when updating status, then it should update successfully")
  void shouldUpdateStatus() {
    MenteeApplication created = createTestApplication(1);

    MenteeApplication updated =
        applicationRepository.updateStatus(
            created.getApplicationId(), ApplicationStatus.MENTOR_ACCEPTED, "Welcome!");

    assertEquals(ApplicationStatus.MENTOR_ACCEPTED, updated.getStatus());
    assertEquals("Welcome!", updated.getMentorResponse());
    assertNotNull(updated.getReviewedAt());
  }

  @Test
  @DisplayName("Given applications exist, when finding by mentee and cycle, then return list")
  void shouldFindByMenteeAndCycle() {
    createTestApplication(1);

    List<MenteeApplication> apps =
        applicationRepository.findByMenteeAndCycle(mentee.getId(), cycle.getCycleId());

    assertThat(apps).hasSize(1);
    assertEquals(mentee.getId(), apps.get(0).getMenteeId());
  }

  @Test
  @DisplayName("Given applications exist, when finding by mentor, then return list")
  void shouldFindByMentor() {
    createTestApplication(1);

    List<MenteeApplication> apps = applicationRepository.findByMentor(mentor.getId());

    assertThat(apps).hasSize(1);
    assertEquals(mentor.getId(), apps.get(0).getMentorId());
  }

  @Test
  @DisplayName("Given applications exist, when counting by mentee and cycle, then return count")
  void shouldCountMenteeApplications() {
    createTestApplication(1);

    Long count = applicationRepository.countMenteeApplications(mentee.getId(), cycle.getCycleId());

    assertEquals(1L, count);
  }

  private MenteeApplication createTestApplication(int priority) {
    return applicationRepository.create(
        MenteeApplication.builder()
            .menteeId(mentee.getId())
            .mentorId(mentor.getId())
            .cycleId(cycle.getCycleId())
            .priorityOrder(priority)
            .status(ApplicationStatus.PENDING)
            .applicationMessage("Message " + priority)
            .build());
  }
}
