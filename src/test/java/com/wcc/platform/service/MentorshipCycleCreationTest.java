package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.wcc.platform.domain.exceptions.DuplicatedException;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleCreateRequest;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.MentorshipCycleRepository;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MentorshipCycleCreationTest {

  @Mock private MentorshipCycleRepository cycleRepository;
  private MentorshipCycleService cycleService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    cycleService = new MentorshipCycleService(cycleRepository);
  }

  @Test
  @DisplayName("Given valid cycle request, when creating cycle, then create draft cycle")
  void shouldCreateDraftCycleWithDefaultMaxMenteesPerMentor() {
    final var request = validCreateRequest(null);
    final var createdCycle = cycleWithStatus(9L, CycleStatus.DRAFT);
    when(cycleRepository.create(org.mockito.ArgumentMatchers.any(MentorshipCycleEntity.class)))
        .thenReturn(createdCycle);

    final var result = cycleService.createCycle(request);

    assertThat(result.getStatus()).isEqualTo(CycleStatus.DRAFT);
    verify(cycleRepository)
        .create(
            org.mockito.ArgumentMatchers.argThat(
                cycle ->
                    cycle.getCycleYear().equals(Year.of(2026))
                        && cycle.getCycleMonth().equals(Month.OCTOBER)
                        && cycle.getMentorshipType() == MentorshipType.AD_HOC
                        && cycle.getMaxMenteesPerMentor() == 5));
  }

  @Test
  @DisplayName("Given duplicate cycle, when creating cycle, then throw conflict exception")
  void shouldPropagateDuplicateCycleException() {
    when(cycleRepository.create(org.mockito.ArgumentMatchers.any(MentorshipCycleEntity.class)))
        .thenThrow(new DuplicatedException("cycle already exists"));
    final var request = validCreateRequest(5);

    assertThatThrownBy(() -> cycleService.createCycle(request))
        .isInstanceOf(DuplicatedException.class)
        .hasMessage("cycle already exists");
  }

  @Test
  @DisplayName(
      "Given registration end before registration start, when creating cycle, then reject request")
  void shouldRejectInvalidRegistrationDateRange() {
    final var request =
        new MentorshipCycleCreateRequest(
            Year.of(2026), Month.OCTOBER, MentorshipType.AD_HOC,
            LocalDate.of(2026, 10, 15), LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 10, 20), null, 5, "October cycle");

    assertThatThrownBy(() -> cycleService.createCycle(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Registration end date");
    verify(cycleRepository, never()).create(org.mockito.ArgumentMatchers.any());
  }

  @Test
  @DisplayName(
      "Given cycle start before registration start, when creating cycle, then reject request")
  void shouldRejectCycleStartBeforeRegistrationStart() {
    final var request =
        new MentorshipCycleCreateRequest(
            Year.of(2026), Month.OCTOBER, MentorshipType.AD_HOC,
            LocalDate.of(2026, 10, 15), LocalDate.of(2026, 10, 20),
            LocalDate.of(2026, 10, 1), null, 5, "October cycle");

    assertThatThrownBy(() -> cycleService.createCycle(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Cycle start date");
    verify(cycleRepository, never()).create(org.mockito.ArgumentMatchers.any());
  }

  @Test
  @DisplayName("Given cycle end before cycle start, when creating cycle, then reject request")
  void shouldRejectCycleEndBeforeCycleStart() {
    final var request =
        new MentorshipCycleCreateRequest(
            Year.of(2026), Month.OCTOBER, MentorshipType.AD_HOC,
            LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 15),
            LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 19),
            5, "October cycle");

    assertThatThrownBy(() -> cycleService.createCycle(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Cycle end date");
    verify(cycleRepository, never()).create(org.mockito.ArgumentMatchers.any());
  }

  private MentorshipCycleCreateRequest validCreateRequest(final Integer maxMenteesPerMentor) {
    return new MentorshipCycleCreateRequest(
        Year.of(2026), Month.OCTOBER, MentorshipType.AD_HOC,
        LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 15),
        LocalDate.of(2026, 10, 20), LocalDate.of(2026, 12, 20),
        maxMenteesPerMentor, "October cycle");
  }

  private MentorshipCycleEntity cycleWithStatus(final Long cycleId, final CycleStatus status) {
    return MentorshipCycleEntity.builder().cycleId(cycleId).status(status).build();
  }
}
