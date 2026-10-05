package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.CycleNotFoundException;
import com.wcc.platform.domain.exceptions.InvalidCycleStatusTransitionException;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleCreateRequest;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.repository.MentorshipCycleRepository;
import java.time.Month;
import java.time.Year;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** Service for mentorship cycle lifecycle management, including status transitions. */
@Service
@RequiredArgsConstructor
public class MentorshipCycleService {

  private static final Map<CycleStatus, Set<CycleStatus>> ALLOWED_TRANSITIONS =
      Map.of(
          CycleStatus.DRAFT, Set.of(CycleStatus.OPEN, CycleStatus.CANCELLED),
          CycleStatus.OPEN, Set.of(CycleStatus.CLOSED, CycleStatus.CANCELLED),
          CycleStatus.CLOSED, Set.of(CycleStatus.IN_PROGRESS, CycleStatus.CANCELLED),
          CycleStatus.IN_PROGRESS, Set.of(CycleStatus.COMPLETED, CycleStatus.CANCELLED),
          CycleStatus.COMPLETED, Set.of(),
          CycleStatus.CANCELLED, Set.of());

  private final MentorshipCycleRepository cycleRepository;

  /**
   * Update the status of a mentorship cycle, enforcing valid state transitions.
   *
   * @param cycleId the ID of the cycle to update
   * @param newStatus the target status
   * @return the updated cycle entity
   * @throws CycleNotFoundException if the cycle is not found
   * @throws InvalidCycleStatusTransitionException if the transition is not permitted
   */
  public MentorshipCycleEntity updateStatus(final Long cycleId, final CycleStatus newStatus) {
    final MentorshipCycleEntity cycle =
        cycleRepository.findById(cycleId).orElseThrow(() -> new CycleNotFoundException(cycleId));

    final CycleStatus current = cycle.getStatus();
    if (!ALLOWED_TRANSITIONS.getOrDefault(current, Set.of()).contains(newStatus)) {
      throw new InvalidCycleStatusTransitionException(current, newStatus);
    }

    return cycleRepository.updateStatus(cycleId, newStatus);
  }

  /**
   * Creates a new mentorship cycle in draft status.
   *
   * @param request cycle creation data
   * @return the created mentorship cycle
   * @throws IllegalArgumentException if the supplied dates are inconsistent
   */
  public MentorshipCycleEntity createCycle(final MentorshipCycleCreateRequest request) {

    if (request.registrationEndDate().isBefore(request.registrationStartDate())) {
      throw new IllegalArgumentException(
          "Registration end date must be greater than or equal to registration start date");
    }

    if (request.cycleStartDate().isBefore(request.registrationStartDate())) {
      throw new IllegalArgumentException(
          "Cycle start date must be greater than or equal to registration start date");
    }

    if (request.cycleEndDate() != null
        && request.cycleEndDate().isBefore(request.cycleStartDate())) {
      throw new IllegalArgumentException(
          "Cycle end date must be greater than or equal to cycle start date");
    }

    final MentorshipCycleEntity cycle =
        MentorshipCycleEntity.builder()
            .cycleYear(Year.of(request.cycleYear()))
            .cycleMonth(Month.of(request.cycleMonth()))
            .mentorshipType(request.mentorshipType())
            .registrationStartDate(request.registrationStartDate())
            .registrationEndDate(request.registrationEndDate())
            .cycleStartDate(request.cycleStartDate())
            .cycleEndDate(request.cycleEndDate())
            .status(CycleStatus.DRAFT)
            .maxMenteesPerMentor(
                request.maxMenteesPerMentor() != null ? request.maxMenteesPerMentor() : 5)
            .description(request.description())
            .build();

    return cycleRepository.create(cycle);
  }
}
