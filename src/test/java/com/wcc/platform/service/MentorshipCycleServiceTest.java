package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.CycleNotFoundException;
import com.wcc.platform.domain.exceptions.InvalidCycleStatusTransitionException;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.repository.MentorshipCycleRepository;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MentorshipCycleServiceTest {

  @Mock private MentorshipCycleRepository cycleRepository;

  private MentorshipCycleService cycleService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    cycleService = new MentorshipCycleService(cycleRepository);
  }

  @Test
  @DisplayName(
      "Given cycle does not exist, when updating status, then throw CycleNotFoundException")
  void shouldThrowWhenCycleNotFound() {
    when(cycleRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> cycleService.updateStatus(99L, CycleStatus.OPEN))
        .isInstanceOf(CycleNotFoundException.class)
        .hasMessageContaining("99");

    verify(cycleRepository, never()).updateStatus(99L, CycleStatus.OPEN);
  }

  @Test
  @DisplayName("Given cycle in DRAFT status, when transitioning to OPEN, then update succeeds")
  void shouldAllowDraftToOpen() {
    final var cycle = cycleWithStatus(1L, CycleStatus.DRAFT);
    final var updated = cycleWithStatus(1L, CycleStatus.OPEN);
    when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycle));
    when(cycleRepository.updateStatus(1L, CycleStatus.OPEN)).thenReturn(updated);

    final MentorshipCycleEntity result = cycleService.updateStatus(1L, CycleStatus.OPEN);

    assertThat(result.getStatus()).isEqualTo(CycleStatus.OPEN);
    verify(cycleRepository).updateStatus(1L, CycleStatus.OPEN);
  }

  @Test
  @DisplayName("Given cycle in DRAFT status, when transitioning to CANCELLED, then update succeeds")
  void shouldAllowDraftToCancelled() {
    final var cycle = cycleWithStatus(1L, CycleStatus.DRAFT);
    final var updated = cycleWithStatus(1L, CycleStatus.CANCELLED);
    when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycle));
    when(cycleRepository.updateStatus(1L, CycleStatus.CANCELLED)).thenReturn(updated);

    final MentorshipCycleEntity result = cycleService.updateStatus(1L, CycleStatus.CANCELLED);

    assertThat(result.getStatus()).isEqualTo(CycleStatus.CANCELLED);
  }

  @Test
  @DisplayName("Given cycle in OPEN status, when transitioning to CLOSED, then update succeeds")
  void shouldAllowOpenToClosed() {
    final var cycle = cycleWithStatus(1L, CycleStatus.OPEN);
    final var updated = cycleWithStatus(1L, CycleStatus.CLOSED);
    when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycle));
    when(cycleRepository.updateStatus(1L, CycleStatus.CLOSED)).thenReturn(updated);

    final MentorshipCycleEntity result = cycleService.updateStatus(1L, CycleStatus.CLOSED);

    assertThat(result.getStatus()).isEqualTo(CycleStatus.CLOSED);
  }

  @Test
  @DisplayName(
      "Given cycle in CLOSED status, when transitioning to IN_PROGRESS, then update succeeds")
  void shouldAllowClosedToInProgress() {
    final var cycle = cycleWithStatus(1L, CycleStatus.CLOSED);
    final var updated = cycleWithStatus(1L, CycleStatus.IN_PROGRESS);
    when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycle));
    when(cycleRepository.updateStatus(1L, CycleStatus.IN_PROGRESS)).thenReturn(updated);

    final MentorshipCycleEntity result = cycleService.updateStatus(1L, CycleStatus.IN_PROGRESS);

    assertThat(result.getStatus()).isEqualTo(CycleStatus.IN_PROGRESS);
  }

  @Test
  @DisplayName(
      "Given cycle in IN_PROGRESS status, when transitioning to COMPLETED, then update succeeds")
  void shouldAllowInProgressToCompleted() {
    final var cycle = cycleWithStatus(1L, CycleStatus.IN_PROGRESS);
    final var updated = cycleWithStatus(1L, CycleStatus.COMPLETED);
    when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycle));
    when(cycleRepository.updateStatus(1L, CycleStatus.COMPLETED)).thenReturn(updated);

    final MentorshipCycleEntity result = cycleService.updateStatus(1L, CycleStatus.COMPLETED);

    assertThat(result.getStatus()).isEqualTo(CycleStatus.COMPLETED);
  }

  @Test
  @DisplayName(
      "Given cycle in DRAFT status, when transitioning to IN_PROGRESS, then throw InvalidCycleStatusTransitionException")
  void shouldRejectDraftToInProgress() {
    final var cycle = cycleWithStatus(1L, CycleStatus.DRAFT);
    when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycle));

    assertThatThrownBy(() -> cycleService.updateStatus(1L, CycleStatus.IN_PROGRESS))
        .isInstanceOf(InvalidCycleStatusTransitionException.class)
        .hasMessageContaining("DRAFT")
        .hasMessageContaining("IN_PROGRESS");

    verify(cycleRepository, never()).updateStatus(1L, CycleStatus.IN_PROGRESS);
  }

  @Test
  @DisplayName(
      "Given cycle in OPEN status, when transitioning to DRAFT, then throw InvalidCycleStatusTransitionException")
  void shouldRejectOpenToDraft() {
    final var cycle = cycleWithStatus(1L, CycleStatus.OPEN);
    when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycle));

    assertThatThrownBy(() -> cycleService.updateStatus(1L, CycleStatus.DRAFT))
        .isInstanceOf(InvalidCycleStatusTransitionException.class)
        .hasMessageContaining("OPEN")
        .hasMessageContaining("DRAFT");
  }

  @Test
  @DisplayName(
      "Given cycle in COMPLETED status, when transitioning to any status, then throw InvalidCycleStatusTransitionException")
  void shouldRejectAnyTransitionFromCompleted() {
    final var cycle = cycleWithStatus(1L, CycleStatus.COMPLETED);
    when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycle));

    assertThatThrownBy(() -> cycleService.updateStatus(1L, CycleStatus.CANCELLED))
        .isInstanceOf(InvalidCycleStatusTransitionException.class)
        .hasMessageContaining("COMPLETED");
  }

  @Test
  @DisplayName(
      "Given cycle in CANCELLED status, when transitioning to any status, then throw InvalidCycleStatusTransitionException")
  void shouldRejectAnyTransitionFromCancelled() {
    final var cycle = cycleWithStatus(1L, CycleStatus.CANCELLED);
    when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycle));

    assertThatThrownBy(() -> cycleService.updateStatus(1L, CycleStatus.OPEN))
        .isInstanceOf(InvalidCycleStatusTransitionException.class)
        .hasMessageContaining("CANCELLED");
  }

  @Test
  @DisplayName(
      "Given cycle in OPEN status, when transitioning to same OPEN status, then throw InvalidCycleStatusTransitionException")
  void shouldRejectSameStatusTransition() {
    final var cycle = cycleWithStatus(1L, CycleStatus.OPEN);
    when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycle));

    assertThatThrownBy(() -> cycleService.updateStatus(1L, CycleStatus.OPEN))
        .isInstanceOf(InvalidCycleStatusTransitionException.class)
        .hasMessageContaining("OPEN");
  }

  @Test
  @DisplayName("Given no year, when getting cycles, then return all cycles")
  void shouldReturnAllCyclesWhenNoYear() {
    final var cycles = List.of(cycleInYear(1L, 2025), cycleInYear(2L, 2026));
    when(cycleRepository.getAll()).thenReturn(cycles);

    final List<MentorshipCycleEntity> result = cycleService.getCycles(null);

    assertThat(result).isEqualTo(cycles);
  }

  @Test
  @DisplayName("Given a year, when getting cycles, then return only cycles in that year")
  void shouldReturnCyclesForYear() {
    final var cycles = List.of(cycleInYear(2L, 2026));
    when(cycleRepository.findByYear(2026)).thenReturn(cycles);

    final List<MentorshipCycleEntity> result = cycleService.getCycles(Year.of(2026));

    assertThat(result).isEqualTo(cycles);
  }

  @Test
  @DisplayName("Given a year with no cycles, when getting cycles, then return an empty list")
  void shouldReturnEmptyListForYearWithNoCycles() {
    when(cycleRepository.findByYear(9999)).thenReturn(List.of());

    final List<MentorshipCycleEntity> result = cycleService.getCycles(Year.of(9999));

    assertThat(result).isEmpty();
  }

  private MentorshipCycleEntity cycleWithStatus(final Long cycleId, final CycleStatus status) {
    return MentorshipCycleEntity.builder().cycleId(cycleId).status(status).build();
  }

  private MentorshipCycleEntity cycleInYear(final Long cycleId, final int year) {
    return MentorshipCycleEntity.builder().cycleId(cycleId).cycleYear(Year.of(year)).build();
  }
}
