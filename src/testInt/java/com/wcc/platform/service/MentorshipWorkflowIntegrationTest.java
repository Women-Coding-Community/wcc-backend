package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMenteeFactories.createMenteeTest;
import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MatchStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.MenteeApplicationDto;
import com.wcc.platform.domain.platform.mentorship.MenteeRegistration;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipMatch;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import com.wcc.platform.repository.postgres.PostgresMenteeTestSetup;
import com.wcc.platform.repository.postgres.PostgresMentorTestSetup;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * End-to-end integration tests for the complete mentorship workflow. Tests the full cycle: cycle
 * management → application → matching.
 *
 * <p>NOTE: Full workflow tests will be enabled once repository create methods are implemented.
 * Currently tests focus on the database schema and read operations.
 */
class MentorshipWorkflowIntegrationTest extends DefaultDatabaseSetup
    implements PostgresMenteeTestSetup, PostgresMentorTestSetup {

  @Autowired private MenteeService menteeService;
  @Autowired private MenteeWorkflowService applicationService;
  @Autowired private MentorshipMatchingService matchingService;

  @Autowired private MentorshipMatchRepository matchRepository;
  @Autowired private MentorshipCycleRepository cycleRepository;
  @Autowired private MenteeApplicationRepository applicationRepository;

  @Autowired private MemberRepository memberRepository;
  @Autowired private MentorRepository mentorRepository;
  @Autowired private MenteeRepository menteeRepository;

  private Mentor mentor1;
  private Mentor mentor2;
  private Mentor mentor3;
  private Mentee mentee;

  @BeforeEach
  void setUp() {
    mentor1 = createMentorTest(null, "Mentor 1", "mentor98@email.com");
    mentor2 = createMentorTest(null, "Mentor 2", "mentor97@email.com");
    mentor3 = createMentorTest(null, "Mentor 3", "mentor96@email.com");
    mentee = createMenteeTest(null, "Mentee", "mentee95@email.com");

    // Clean up before starting
    deleteMentor(mentor1, mentorRepository, memberRepository);
    deleteMentor(mentor2, mentorRepository, memberRepository);
    deleteMentor(mentor3, mentorRepository, memberRepository);
    deleteMentee(mentee, menteeRepository, memberRepository);

    cycleRepository
        .findByYearAndType(Year.of(2026), MentorshipType.LONG_TERM)
        .ifPresent(
            c -> {
              matchRepository
                  .findByCycle(c.getCycleId())
                  .forEach(m -> matchRepository.deleteById(m.getMatchId()));
              applicationRepository
                  .findByMenteeAndCycle(null, c.getCycleId())
                  .forEach(
                      a -> {
                        applicationRepository.deleteById(a.getApplicationId());
                      });
              cycleRepository.deleteById(c.getCycleId());
            });

    // Setup cycle and mentors
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

    mentor1 = mentorRepository.create(mentor1);
    mentor2 = mentorRepository.create(mentor2);
    mentor3 = mentorRepository.create(mentor3);
  }

  @Test
  @DisplayName(
      "Given database migrations ran, when checking schema, then all tables should exist with correct structure")
  void shouldHaveCorrectDatabaseSchema() {
    final List<MentorshipCycleEntity> cycles = cycleRepository.getAll();
    assertThat(cycles).isNotEmpty();
    assertThat(cycles).hasSizeGreaterThanOrEqualTo(8); // V18 seeds 8 cycles

    // Verify at least one cycle is open
    final Optional<MentorshipCycleEntity> openCycle = cycleRepository.findOpenCycle();
    assertThat(openCycle).isPresent();
    assertThat(openCycle.get().getStatus()).isEqualTo(CycleStatus.OPEN);

    // Verify cycle has all required fields
    final MentorshipCycleEntity cycle = openCycle.get();
    assertThat(cycle.getCycleId()).isNotNull();
    assertThat(cycle.getCycleYear()).isEqualTo(Year.of(2026));
    assertThat(cycle.getMentorshipType()).isEqualTo(MentorshipType.LONG_TERM);
    assertThat(cycle.getCycleMonth()).isNotNull();
    assertThat(cycle.getRegistrationStartDate()).isNotNull();
    assertThat(cycle.getRegistrationEndDate()).isNotNull();
    assertThat(cycle.getCycleStartDate()).isNotNull();
    assertThat(cycle.getMaxMenteesPerMentor()).isEqualTo(6);
    assertThat(cycle.getDescription()).isEqualTo("Test Cycle");
  }

  @Test
  @DisplayName(
      "Given cycle repository, when finding cycles by different statuses, then it should return correct results")
  void shouldQueryCyclesByStatus() {
    // Test OPEN cycles
    final List<MentorshipCycleEntity> openCycles = cycleRepository.findByStatus(CycleStatus.OPEN);
    assertThat(openCycles).isNotEmpty();
    assertThat(openCycles).allMatch(cycle -> cycle.getStatus() == CycleStatus.OPEN);

    // Test DRAFT cycles
    final List<MentorshipCycleEntity> draftCycles = cycleRepository.findByStatus(CycleStatus.DRAFT);
    assertThat(draftCycles).isNotEmpty();
    assertThat(draftCycles).allMatch(cycle -> cycle.getStatus() == CycleStatus.DRAFT);

    // Test that all cycles are accounted for
    final List<MentorshipCycleEntity> allCycles = cycleRepository.getAll();
    assertThat(allCycles.size()).isEqualTo(openCycles.size() + draftCycles.size());
  }

  @Test
  @DisplayName(
      "Given match repository, when checking for non-existent matches, then it should handle gracefully")
  void shouldHandleNonExistentMatchQueries() {
    // Verify repository handles non-existent data without errors
    assertThat(matchRepository.isMenteeMatchedInCycle(99L, 1L)).isFalse();
    assertThat(matchRepository.countActiveMenteesByMentorAndCycle(99L, 1L)).isZero();
    assertThat(matchRepository.findById(99L)).isEmpty();
    assertThat(matchRepository.findActiveMentorByMentee(99L)).isEmpty();
    assertThat(matchRepository.findActiveMenteesByMentor(99L)).isEmpty();
    assertThat(matchRepository.findByCycle(99L)).isEmpty();
  }

  /**
   * This test documents the intended complete workflow. It will be fully functional once repository
   * create methods are implemented.
   */
  @Test
  @DisplayName(
      "Complete Long-Term Mentorship workflow from application to match confirmation, "
          + "Session tracking and COMPLETED cycle/sessions")
  void documentCompleteWorkflow() {
    // STEP 1: Get open cycle
    final Optional<MentorshipCycleEntity> openCycle = cycleRepository.findOpenCycle();
    assertThat(openCycle).isPresent();

    var cycleId = openCycle.get().getCycleId();

    // STEP 2: Mentee submits applications to multiple mentors with priority
    var registration =
        new MenteeRegistration(
            mentee,
            MentorshipType.LONG_TERM,
            Year.of(2026),
            List.of(
                new MenteeApplicationDto(null, mentor1.getId(), 1),
                new MenteeApplicationDto(null, mentor2.getId(), 2),
                new MenteeApplicationDto(null, mentor3.getId(), 3)));

    menteeService.saveRegistration(registration);

    List<MenteeApplication> applications =
        applicationRepository.findByMenteeAndCycle(mentee.getId(), cycleId);
    assertThat(applications).hasSize(3);
    assertThat(applications.stream().anyMatch(a -> a.getPriorityOrder() == 1)).isTrue();

    var acceptedApp =
        applications.stream().filter(a -> a.getPriorityOrder() == 1).findFirst().orElseThrow();

    // STEP 3: First priority mentor accepts
    MenteeApplication accepted =
        applicationService.acceptApplication(
            acceptedApp.getApplicationId(), "Happy to mentor you!");
    assertThat(accepted.getStatus()).isEqualTo(ApplicationStatus.MENTOR_ACCEPTED);

    // STEP 4: Admin/Mentorship team confirms the match
    MentorshipMatch match = matchingService.confirmMatch(accepted.getApplicationId());
    assertThat(match.getStatus()).isEqualTo(MatchStatus.ACTIVE);

    // STEP 5: Verify other applications are rejected
    List<MenteeApplication> menteeApps =
        applicationService.getMenteeApplications(mentee.getId(), cycleId);
    assertThat(menteeApps)
        .filteredOn(app -> !app.getApplicationId().equals(accepted.getApplicationId()))
        .allMatch(app -> app.getStatus() == ApplicationStatus.REJECTED);

    // STEP 6: Verify mentee is marked as matched for the cycle
    boolean isMatched = matchRepository.isMenteeMatchedInCycle(mentee.getId(), cycleId);
    assertThat(isMatched).isTrue();

    // STEP 7: Track session participation
    MentorshipMatch updated = matchingService.incrementSessionCount(match.getMatchId());
    assertThat(updated.getTotalSessions()).isEqualTo(1);
    updated = matchingService.incrementSessionCount(match.getMatchId());
    assertThat(updated.getTotalSessions()).isEqualTo(2);

    // STEP 8: Complete the mentorship
    MentorshipMatch completed =
        matchingService.completeMatch(match.getMatchId(), "Great mentorship experience");
    assertThat(completed.getStatus()).isEqualTo(MatchStatus.COMPLETED);
  }

  @Test
  @DisplayName(
      "Given services are autowired, when checking dependency injection, then all services should be available")
  void shouldHaveAllRequiredServices() {
    assertThat(cycleRepository).isNotNull();
    assertThat(applicationRepository).isNotNull();
    assertThat(matchRepository).isNotNull();
    assertThat(applicationService).isNotNull();
    assertThat(matchingService).isNotNull();
  }

  @Test
  @DisplayName(
      "Given database schema, when verifying year tracking, then mentorship types should support year column")
  void shouldSupportYearTrackingInMentorshipTypes() {
    final List<MentorshipCycleEntity> cycles = cycleRepository.getAll();
    assertThat(cycles).isNotEmpty();

    // All cycles should have a valid year
    assertThat(cycles)
        .allMatch(cycle -> cycle.getCycleYear() != null && cycle.getCycleYear().getValue() > 2025);
  }
}
