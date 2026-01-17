package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration tests for MentorshipCycleRepository with PostgreSQL. Tests cycle queries and
 * management operations.
 */
class MentorshipCycleIntegrationTest extends DefaultDatabaseSetup {

  @Autowired private MentorshipCycleRepository cycleRepository;

  @Test
  @DisplayName(
      "Given database is seeded with cycles, when finding open cycle, then it should return the open cycle")
  void shouldFindOpenCycle() {
    final Optional<MentorshipCycleEntity> openCycle = cycleRepository.findOpenCycle();

    assertThat(openCycle).isPresent();
    assertThat(openCycle.get().getStatus()).isEqualTo(CycleStatus.OPEN);
  }

  @Test
  @DisplayName(
      "Given database is seeded, when finding all cycles, then it should return all cycles")
  void shouldFindAllCycles() {
    final List<MentorshipCycleEntity> allCycles = cycleRepository.getAll();

    assertThat(allCycles).isNotEmpty();
    // V18 migration seeds 8 cycles for 2026
    assertThat(allCycles.size()).isGreaterThanOrEqualTo(8);
  }

  @Test
  @DisplayName(
      "Given database is seeded, when finding cycles by status OPEN, then it should return open cycles")
  void shouldFindCyclesByStatusOpen() {
    final List<MentorshipCycleEntity> openCycles = cycleRepository.findByStatus(CycleStatus.OPEN);

    assertThat(openCycles).isNotEmpty();
    assertThat(openCycles).allMatch(cycle -> cycle.getStatus() == CycleStatus.OPEN);
  }

  @Test
  @DisplayName(
      "Given database is seeded, when finding cycles by status DRAFT, then it should return draft cycles")
  void shouldFindCyclesByStatusDraft() {
    final List<MentorshipCycleEntity> draftCycles = cycleRepository.findByStatus(CycleStatus.DRAFT);

    assertThat(draftCycles).isNotEmpty();
    assertThat(draftCycles).allMatch(cycle -> cycle.getStatus() == CycleStatus.DRAFT);
  }

  @Test
  @DisplayName(
      "Given database is seeded, when finding cycle by ID, then it should return the correct cycle")
  void shouldFindCycleById() {
    // First get all cycles to find a valid ID
    final List<MentorshipCycleEntity> allCycles = cycleRepository.getAll();
    assertThat(allCycles).isNotEmpty();

    final Long validCycleId = allCycles.getFirst().getCycleId();
    final Optional<MentorshipCycleEntity> found = cycleRepository.findById(validCycleId);

    assertThat(found).isPresent();
    assertThat(found.get().getCycleId()).isEqualTo(validCycleId);
  }

  @Test
  @DisplayName("Given non-existent cycle ID, when finding by ID, then it should return empty")
  void shouldReturnEmptyForNonExistentCycleId() {
    final Optional<MentorshipCycleEntity> found = cycleRepository.findById(99L);

    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName(
      "Given seeded cycles, when checking cycle properties, then they should have valid data")
  void shouldHaveValidCycleData() {
    final List<MentorshipCycleEntity> allCycles = cycleRepository.getAll();
    assertThat(allCycles).isNotEmpty();

    final MentorshipCycleEntity cycle = allCycles.getFirst();

    assertThat(cycle.getCycleId()).isNotNull();
    assertThat(cycle.getCycleYear()).isNotNull();
    assertThat(cycle.getMentorshipType()).isNotNull();
    assertThat(cycle.getStatus()).isNotNull();
    assertThat(cycle.getRegistrationStartDate()).isNotNull();
    assertThat(cycle.getRegistrationEndDate()).isNotNull();
    assertThat(cycle.getCycleStartDate()).isNotNull();
    assertThat(cycle.getMaxMenteesPerMentor()).isGreaterThan(0);
  }
}
