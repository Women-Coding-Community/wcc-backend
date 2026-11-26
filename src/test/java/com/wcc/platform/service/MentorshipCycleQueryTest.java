package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.repository.MentorshipCycleRepository;
import java.time.Year;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MentorshipCycleQueryTest {

  @Mock private MentorshipCycleRepository cycleRepository;
  private MentorshipCycleService cycleService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    cycleService = new MentorshipCycleService(cycleRepository);
  }

  @Test
  @DisplayName("Given no year, when getting cycles, then return all cycles")
  void shouldReturnAllCyclesWhenNoYear() {
    final var cycles = List.of(cycleInYear(1L, 2025), cycleInYear(2L, 2026));
    when(cycleRepository.getAll()).thenReturn(cycles);

    final var result = cycleService.getCycles(null);

    assertThat(result).isEqualTo(cycles);
  }

  @Test
  @DisplayName("Given a year, when getting cycles, then return only cycles in that year")
  void shouldReturnCyclesForYear() {
    final var cycles = List.of(cycleInYear(2L, 2026));
    when(cycleRepository.findByYear(2026)).thenReturn(cycles);

    final var result = cycleService.getCycles(Year.of(2026));

    assertThat(result).isEqualTo(cycles);
  }

  @Test
  @DisplayName("Given a year with no cycles, when getting cycles, then return an empty list")
  void shouldReturnEmptyListForYearWithNoCycles() {
    when(cycleRepository.findByYear(9999)).thenReturn(List.of());

    final var result = cycleService.getCycles(Year.of(9999));

    assertThat(result).isEmpty();
  }

  private MentorshipCycleEntity cycleInYear(final Long cycleId, final int year) {
    return MentorshipCycleEntity.builder().cycleId(cycleId).cycleYear(Year.of(year)).build();
  }
}
