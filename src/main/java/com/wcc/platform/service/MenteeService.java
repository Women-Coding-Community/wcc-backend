package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.DuplicatedMemberException;
import com.wcc.platform.domain.exceptions.InvalidMentorshipTypeException;
import com.wcc.platform.domain.exceptions.MentorshipCycleClosedException;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycle;
import com.wcc.platform.repository.MenteeRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MenteeService {

  private final MenteeRepository menteeRepository;
  private final MentorshipService mentorshipService;

  /**
   * Create a mentee record.
   *
   * @return Mentee record created successfully.
   */
  public Mentee create(final Mentee mentee) {
    menteeRepository
        .findById(mentee.getId())
        .ifPresent(
            existing -> {
              throw new DuplicatedMemberException(String.valueOf(existing.getId()));
            });

    validateMentorshipCycle(mentee);

    return menteeRepository.create(mentee);
  }

  /**
   * Validates if the mentee can register based on the current mentorship cycle.
   *
   * @param mentee The mentee to validate
   * @throws MentorshipCycleClosedException if the current cycle is closed
   * @throws InvalidMentorshipTypeException if mentee's type doesn't match current cycle
   */
  private void validateMentorshipCycle(final Mentee mentee) {
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
