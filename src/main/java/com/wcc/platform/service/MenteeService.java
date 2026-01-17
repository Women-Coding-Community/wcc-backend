package com.wcc.platform.service;

import com.wcc.platform.configuration.MentorshipConfig;
import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.InvalidMentorshipTypeException;
import com.wcc.platform.domain.exceptions.MentorshipCycleClosedException;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycle;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MenteeService {

  private final MenteeRepository menteeRepository;
  private final MentorshipService mentorshipService;
  private final MentorshipConfig mentorshipConfig;
  private final MentorshipCycleRepository cycleRepository;

  /**
   * Create a mentee record for a specific cycle year.
   *
   * @param mentee The mentee to create
   * @param cycleYear The year of the mentorship cycle
   * @return Mentee record created successfully.
   */
  public Mentee create(final Mentee mentee, final Integer cycleYear) {
    menteeRepository
        .findById(mentee.getId())
        .ifPresent(
            existing -> {
              throw new DuplicatedMemberException(String.valueOf(existing.getId()));
            });

    if (mentorshipConfig.getValidation().isEnabled()) {
      validateMentorshipCycle(mentee, cycleYear);
      validateNotAlreadyRegisteredForCycle(
          mentee.getId(), cycleYear, mentee.getMentorshipType());
    }

    return menteeRepository.create(mentee, cycleYear);
  }

  /**
   * Create a mentee record using current year.
   *
   * @param mentee The mentee to create
   * @return Mentee record created successfully.
   * @deprecated Use {@link #create(Mentee, Integer)} instead
   */
  @Deprecated
  public Mentee create(final Mentee mentee) {
    return create(mentee, java.time.Year.now().getValue());
  }

  /**
   * Validates if the mentee can register based on the mentorship cycle.
   *
   * @param mentee The mentee to validate
   * @param cycleYear The year of the cycle
   * @throws MentorshipCycleClosedException if no open cycle exists
   * @throws InvalidMentorshipTypeException if mentee's type doesn't match cycle
   */
  private void validateMentorshipCycle(final Mentee mentee, final Integer cycleYear) {
    // First try new cycle repository
    final var openCycle =
        cycleRepository.findByYearAndType(cycleYear, mentee.getMentorshipType());

    if (openCycle.isPresent()) {
      final MentorshipCycleEntity cycle = openCycle.get();
      if (cycle.getStatus() != CycleStatus.OPEN) {
        throw new MentorshipCycleClosedException(
            String.format(
                "Mentorship cycle for %s in %d is %s. Registration is not available.",
                mentee.getMentorshipType(), cycleYear, cycle.getStatus()));
      }
      return;
    }

    // Fallback to old mentorship service validation for backward compatibility
    final MentorshipCycle currentCycle = mentorshipService.getCurrentCycle();

    if (currentCycle == MentorshipService.CYCLE_CLOSED) {
      throw new MentorshipCycleClosedException(
          "Mentorship cycle is currently closed. Registration is not available.");
    }

    if (mentee.getMentorshipType() != currentCycle.cycle()) {
      throw new InvalidMentorshipTypeException(
          String.format(
              "Mentee mentorship type '%s' does not match current cycle type '%s'.",
              mentee.getMentorshipType(), currentCycle.cycle()));
    }
  }

  /**
   * Validates that the mentee hasn't already registered for the cycle/year combination.
   *
   * @param menteeId The mentee ID
   * @param cycleYear The year of the cycle
   * @param mentorshipType The mentorship type
   * @throws DuplicatedMemberException if already registered
   */
  private void validateNotAlreadyRegisteredForCycle(
      final Long menteeId,
      final Integer cycleYear,
      final com.wcc.platform.domain.platform.mentorship.MentorshipType mentorshipType) {
    final boolean alreadyRegistered =
        menteeRepository.existsByMenteeYearType(menteeId, cycleYear, mentorshipType);

    if (alreadyRegistered) {
      throw new DuplicatedMemberException(
          String.format(
              "Mentee %d already registered for %s in %d", menteeId, mentorshipType, cycleYear));
    }
  }

  /**
   * Return all stored mentees.
   *
   * @return List of mentees.
   */
  public List<Mentee> getAllMentees() {
    final var allMentees = menteeRepository.getAll();
    if (allMentees == null) {
      return List.of();
    }
    return allMentees;
  }
}
