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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration tests for the new query methods added to PostgresMenteeApplicationRepository:
 * findPendingByMenteeId.
 */
class PostgresMenteeApplicationRepositoryNewMethodsIntegrationTest extends DefaultDatabaseSetup {

  @Autowired private PostgresMenteeApplicationRepository applicationRepository;
  @Autowired private PostgresMenteeRepository menteeRepository;
  @Autowired private PostgresMentorRepository mentorRepository;
  @Autowired private PostgresMentorshipCycleRepository cycleRepository;
  @Autowired private PostgresMemberRepository memberRepository;

  private Mentee mentee;
  private Mentor mentorA;
  private Mentor mentorB;
  private MentorshipCycleEntity cycle;

  @BeforeEach
  void setUp() {
    memberRepository.deleteByEmail("mentor_new_a@test.com");
    memberRepository.deleteByEmail("mentor_new_b@test.com");
    memberRepository.deleteByEmail("mentee_new@test.com");
    cycleRepository
        .findByYearAndType(Year.of(2027), MentorshipType.LONG_TERM)
        .ifPresent(c -> cycleRepository.deleteById(c.getCycleId()));

    cycle =
        cycleRepository.create(
            MentorshipCycleEntity.builder()
                .cycleYear(Year.of(2027))
                .mentorshipType(MentorshipType.LONG_TERM)
                .cycleMonth(Month.MARCH)
                .registrationStartDate(LocalDate.now().minusDays(1))
                .registrationEndDate(LocalDate.now().plusDays(10))
                .cycleStartDate(LocalDate.now().plusDays(15))
                .status(CycleStatus.OPEN)
                .maxMenteesPerMentor(3)
                .description("New Methods Test Cycle")
                .build());

    mentorA =
        mentorRepository.create(
            SetupMentorFactories.createMentorTest(null, "Mentor A", "mentor_new_a@test.com"));

    mentorB =
        mentorRepository.create(
            SetupMentorFactories.createMentorTest(null, "Mentor B", "mentor_new_b@test.com"));

    mentee =
        menteeRepository.create(
            SetupMenteeFactories.createMenteeTest(null, "Mentee New", "mentee_new@test.com"));
  }

  @AfterEach
  void tearDown() {
    if (mentee != null) {
      menteeRepository.deleteById(mentee.getId());
      memberRepository.deleteById(mentee.getId());
    }
    if (mentorA != null) {
      mentorRepository.deleteById(mentorA.getId());
      memberRepository.deleteById(mentorA.getId());
    }
    if (mentorB != null) {
      mentorRepository.deleteById(mentorB.getId());
      memberRepository.deleteById(mentorB.getId());
    }
    if (cycle != null) {
      cycleRepository.deleteById(cycle.getCycleId());
    }
  }

  @Test
  @DisplayName(
      "Given mentee has two PENDING apps to different mentors, when finding pending by menteeId, "
          + "then both are returned")
  void shouldFindPendingByMenteeId() {
    final MenteeApplication app1 = createApplication(mentorA.getId(), 1, ApplicationStatus.PENDING);
    final MenteeApplication app2 = createApplication(mentorB.getId(), 2, ApplicationStatus.PENDING);

    final List<MenteeApplication> result =
        applicationRepository.findPendingByMenteeId(mentee.getId());

    assertThat(result).hasSize(2);
    assertThat(result)
        .extracting(MenteeApplication::getApplicationId)
        .containsExactlyInAnyOrder(app1.getApplicationId(), app2.getApplicationId());
  }

  @Test
  @DisplayName(
      "Given mentee has PENDING and REJECTED apps, when finding pending by menteeId, "
          + "then only PENDING is returned")
  void shouldReturnOnlyPendingWhenFindingPendingByMenteeId() {
    var pending = createApplication(mentorA.getId(), 1, ApplicationStatus.PENDING);
    var toReject = createApplication(mentorB.getId(), 2, ApplicationStatus.PENDING);
    applicationRepository.updateStatus(
        toReject.getApplicationId(), ApplicationStatus.REJECTED, "Does not qualify");

    final List<MenteeApplication> result =
        applicationRepository.findPendingByMenteeId(mentee.getId());

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getApplicationId()).isEqualTo(pending.getApplicationId());
  }

  @Test
  @DisplayName(
      "Given mentee has no applications, when finding pending by menteeId, "
          + "then empty list is returned")
  void shouldReturnEmptyListWhenNoAppsForMentee() {
    final List<MenteeApplication> result =
        applicationRepository.findPendingByMenteeId(mentee.getId());

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName(
      "Given two PENDING apps for mentee, when finding pending by menteeId, "
          + "then results are ordered by priority")
  void shouldReturnAppsOrderedByPriorityWhenFindingPendingByMenteeId() {
    createApplication(mentorB.getId(), 2, ApplicationStatus.PENDING);
    createApplication(mentorA.getId(), 1, ApplicationStatus.PENDING);

    final List<MenteeApplication> result =
        applicationRepository.findPendingByMenteeId(mentee.getId());

    assertThat(result).hasSize(2);
    assertThat(result.getFirst().getPriorityOrder()).isEqualTo(1);
    assertThat(result.getLast().getPriorityOrder()).isEqualTo(2);
  }

  private MenteeApplication createApplication(
      final Long mentorId, final int priority, final ApplicationStatus status) {
    final MenteeApplication app =
        applicationRepository.create(
            MenteeApplication.builder()
                .menteeId(mentee.getId())
                .mentorId(mentorId)
                .cycleId(cycle.getCycleId())
                .priorityOrder(priority)
                .status(ApplicationStatus.PENDING)
                .whyMentor("Because of their expertise")
                .build());

    if (status != ApplicationStatus.PENDING) {
      return applicationRepository.updateStatus(app.getApplicationId(), status, "Status update");
    }
    return app;
  }
}
