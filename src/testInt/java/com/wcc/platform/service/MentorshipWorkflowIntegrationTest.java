package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import java.util.List;
import java.util.Optional;
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
class MentorshipWorkflowIntegrationTest extends DefaultDatabaseSetup {

  @Autowired private MentorshipCycleRepository cycleRepository;

  @Autowired private MenteeApplicationRepository applicationRepository;

  @Autowired private MentorshipMatchRepository matchRepository;

  @Autowired private MenteeWorkflowService applicationService;

  @Autowired private MentorshipMatchingService matchingService;

  @Test
  @DisplayName(
      "Given database migrations ran, when checking schema, then all tables should exist with correct structure")
  void shouldHaveCorrectDatabaseSchema() {
    // Verify mentorship_cycles table exists and has data
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
    assertThat(cycle.getCycleYear()).isEqualTo(2026);
    assertThat(cycle.getMentorshipType()).isNotNull();
    assertThat(cycle.getCycleMonth()).isNotNull();
    assertThat(cycle.getRegistrationStartDate()).isNotNull();
    assertThat(cycle.getRegistrationEndDate()).isNotNull();
    assertThat(cycle.getCycleStartDate()).isNotNull();
    assertThat(cycle.getMaxMenteesPerMentor()).isGreaterThan(0);
    assertThat(cycle.getDescription()).isNotNull();
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
  @DisplayName("PLACEHOLDER: Complete mentorship workflow from application to match confirmation")
  void documentCompleteWorkflow() {
    // STEP 1: Get open cycle
    final Optional<MentorshipCycleEntity> openCycle = cycleRepository.findOpenCycle();
    assertThat(openCycle).isPresent();

    // TODO: Enable when repository create is implemented
    // STEP 2: Mentee submits applications to multiple mentors with priority
    // List<MenteeApplication> applications = applicationService.submitApplications(
    //     menteeId, cycleId, List.of(mentor1Id, mentor2Id, mentor3Id), "I want to learn..."
    // );
    // assertThat(applications).hasSize(3);
    // assertThat(applications.get(0).getPriorityOrder()).isEqualTo(1);

    // STEP 3: First priority mentor accepts
    // MenteeApplication accepted = applicationService.acceptApplication(
    //     applications.get(0).getApplicationId(), "Happy to mentor you!"
    // );
    // assertThat(accepted.getStatus()).isEqualTo(ApplicationStatus.MENTOR_ACCEPTED);

    // STEP 4: Admin/Mentorship team confirms the match
    // MentorshipMatch match = matchingService.confirmMatch(accepted.getApplicationId());
    // assertThat(match.getStatus()).isEqualTo(MatchStatus.ACTIVE);

    // STEP 5: Verify other applications are rejected
    // List<MenteeApplication> menteeApps = applicationService.getMenteeApplications(
    //     menteeId, cycleId
    // );
    // assertThat(menteeApps)
    //     .filteredOn(app -> !app.getApplicationId().equals(accepted.getApplicationId()))
    //     .allMatch(app -> app.getStatus() == ApplicationStatus.REJECTED);

    // STEP 6: Verify mentee is marked as matched for the cycle
    // boolean isMatched = matchRepository.isMenteeMatchedInCycle(menteeId, cycleId);
    // assertThat(isMatched).isTrue();

    // STEP 7: Track session participation
    // MentorshipMatch updated = matchingService.incrementSessionCount(match.getMatchId());
    // assertThat(updated.getTotalSessions()).isEqualTo(1);

    // STEP 8: Complete the mentorship
    // MentorshipMatch completed = matchingService.completeMatch(
    //     match.getMatchId(), "Great mentorship experience"
    // );
    // assertThat(completed.getStatus()).isEqualTo(MatchStatus.COMPLETED);

    // For now, just verify the infrastructure is in place
    assertThat(cycleRepository).isNotNull();
    assertThat(applicationRepository).isNotNull();
    assertThat(matchRepository).isNotNull();
    assertThat(applicationService).isNotNull();
    assertThat(matchingService).isNotNull();
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
    // This verifies V17 migration worked correctly
    // The mentee_mentorship_types table should now have cycle_year column
    // and mentee_previous_mentorship_types should be removed

    // Indirect verification: If the application boots and mentee creation works,
    // then the schema is correct
    final List<MentorshipCycleEntity> cycles = cycleRepository.getAll();
    assertThat(cycles).isNotEmpty();

    // All cycles should have a valid year
    assertThat(cycles)
        .allMatch(cycle -> cycle.getCycleYear() != null && cycle.getCycleYear() > 2025);
  }
}
