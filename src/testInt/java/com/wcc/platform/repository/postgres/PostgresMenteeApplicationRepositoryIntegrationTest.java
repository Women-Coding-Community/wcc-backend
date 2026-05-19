package com.wcc.platform.repository.postgres;

import static org.assertj.core.api.Assertions.assertThat;

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

    assertThat(created.getApplicationId()).isNotNull();
    assertThat(created.getMenteeId()).isEqualTo(mentee.getId());
    assertThat(created.getMentorId()).isEqualTo(mentor.getId());
    assertThat(created.getCycleId()).isEqualTo(cycle.getCycleId());
    assertThat(created.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    assertThat(created.getApplicationMessage()).isEqualTo("I want to learn");
    assertThat(created.getAppliedAt()).isNotNull();
  }

  @Test
  @DisplayName("Given existing application, when finding by ID, then it should return application")
  void shouldFindById() {
    MenteeApplication created = createTestApplication(1);

    Optional<MenteeApplication> found = applicationRepository.findById(created.getApplicationId());

    assertThat(found).isPresent();
    assertThat(found.get().getApplicationId()).isEqualTo(created.getApplicationId());
  }

  @Test
  @DisplayName(
      "Given existing application, when updating status, then it should update successfully")
  void shouldUpdateStatus() {
    MenteeApplication created = createTestApplication(1);

    MenteeApplication updated =
        applicationRepository.updateStatus(
            created.getApplicationId(), ApplicationStatus.MENTOR_ACCEPTED, "Welcome!");

    assertThat(updated.getStatus()).isEqualTo(ApplicationStatus.MENTOR_ACCEPTED);
    assertThat(updated.getMentorResponse()).isEqualTo("Welcome!");
    assertThat(updated.getReviewedAt()).isNotNull();
  }

  @Test
  @DisplayName("Given applications exist, when finding by mentee and cycle, then return list")
  void shouldFindByMenteeAndCycle() {
    createTestApplication(1);

    List<MenteeApplication> apps =
        applicationRepository.findByMenteeAndCycle(mentee.getId(), cycle.getCycleId());

    assertThat(apps).hasSize(1);
    assertThat(apps.getFirst().getMenteeId()).isEqualTo(mentee.getId());
  }

  @Test
  @DisplayName("Given applications exist, when finding by mentor, then return list")
  void shouldFindByMentor() {
    createTestApplication(1);

    List<MenteeApplication> apps = applicationRepository.findByMentor(mentor.getId());

    assertThat(apps).hasSize(1);
    assertThat(apps.getFirst().getMentorId()).isEqualTo(mentor.getId());
  }

  @Test
  @DisplayName("Given applications exist, when finding by cycle and statuses, then return list")
  void shouldFindByCycleAndStatuses() {
    createTestApplication(1);

    List<MenteeApplication> apps =
        applicationRepository.findByCycleAndStatuses(
            cycle.getCycleId(), List.of(ApplicationStatus.PENDING));

    assertThat(apps).hasSize(1);
    assertThat(apps.getFirst().getStatus()).isEqualTo(ApplicationStatus.PENDING);
  }

  @Test
  @DisplayName(
      "Given applications exist, when finding by cycle, statuses and mentor, then return list")
  void shouldFindByCycleAndStatusesAndMentor() {
    createTestApplication(1);

    List<MenteeApplication> apps =
        applicationRepository.findByCycleAndStatusesAndMentor(
            cycle.getCycleId(), List.of(ApplicationStatus.PENDING), mentor.getId());

    assertThat(apps).hasSize(1);
    assertThat(apps.getFirst().getMentorId()).isEqualTo(mentor.getId());
    assertThat(apps.getFirst().getStatus()).isEqualTo(ApplicationStatus.PENDING);
  }

  @Test
  @DisplayName("Given applications exist, when counting by mentee and cycle, then return count")
  void shouldCountMenteeApplications() {
    createTestApplication(1);

    Long count = applicationRepository.countMenteeApplications(mentee.getId(), cycle.getCycleId());

    assertThat(count).isEqualTo(1L);
  }

  @Test
  @DisplayName("Given application with whyMentor, when creating, then field should be persisted")
  void shouldPersistWhyMentorField() {
    MenteeApplication application =
        MenteeApplication.builder()
            .menteeId(mentee.getId())
            .mentorId(mentor.getId())
            .cycleId(cycle.getCycleId())
            .priorityOrder(1)
            .status(ApplicationStatus.PENDING)
            .applicationMessage("I want to learn")
            .whyMentor("Because this mentor has expertise in my field")
            .build();

    MenteeApplication created = applicationRepository.create(application);

    assertThat(created.getWhyMentor()).isNotNull();
    assertThat(created.getWhyMentor()).isEqualTo("Because this mentor has expertise in my field");
  }

  @Test
  @DisplayName("Given application without whyMentor, when creating, then field should be null")
  void shouldAllowNullWhyMentorField() {
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

    assertThat(created.getApplicationId()).isNotNull();
    assertThat(created.getApplicationMessage()).isEqualTo("I want to learn");
  }

  private MenteeApplication createTestApplication(final int priority) {
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
