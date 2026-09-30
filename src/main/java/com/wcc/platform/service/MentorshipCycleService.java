package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.CycleNotFoundException;
import com.wcc.platform.domain.exceptions.InvalidCycleStatusTransitionException;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.repository.MentorshipCycleRepository;
import java.util.List;
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

  private static final int MIN_YEAR = 1000;
  private static final int MAX_YEAR = 9999;

  private final MentorshipCycleRepository cycleRepository;

  /**
   * Get mentorship cycles, optionally filtered by year.
   *
   * @param year optional cycle year filter
   * @return list of mentorship cycles
   * @throws IllegalArgumentException if the year is not in YYYY format
   */
  public List<MentorshipCycleEntity> getCycles(final Integer year) {
    if (year == null) {
      return cycleRepository.getAll();
    }
    if (year < MIN_YEAR || year > MAX_YEAR) {
      throw new IllegalArgumentException("Year must be in YYYY format");
    }
    return cycleRepository.findByYear(year);
  }

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
}
