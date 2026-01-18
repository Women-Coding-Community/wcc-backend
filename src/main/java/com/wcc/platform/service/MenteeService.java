package com.wcc.platform.service;

import com.wcc.platform.configuration.MentorshipConfig;
import com.wcc.platform.domain.exceptions.InvalidMentorshipTypeException;
import com.wcc.platform.domain.exceptions.MenteeRegistrationLimitExceededException;
import com.wcc.platform.domain.exceptions.MentorshipCycleClosedException;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeRegistration;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycle;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import java.time.Year;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MenteeService {

  private final MentorshipService mentorshipService;
  private final MentorshipConfig mentorshipConfig;
  private final MentorshipCycleRepository cycleRepository;
  private final MenteeApplicationRepository registrationsRepo;
  private final MenteeRepository menteeRepository;

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

  /**
   * Create a mentee menteeRegistration for a mentorship cycle.
   *
   * @param menteeRegistration The menteeRegistration to create
   * @return Mentee record created successfully.
   */
  public Mentee saveRegistration(final MenteeRegistration menteeRegistration) {
    final var cycle =
        getMentorshipCycle(menteeRegistration.mentorshipType(), menteeRegistration.cycleYear());

    var menteeId = menteeRegistration.mentee().getId();
    var registrations = registrationsRepo.countMenteeApplications(menteeId, cycle.getCycleId());
    if (registrations != null && registrations > 0) {
      updateMenteeApplications(menteeRegistration, menteeId, cycle);
      return menteeRegistration.mentee();
    } else {
      return createMenteeAndApplications(menteeRegistration, cycle);
    }
  }

  private Mentee createMenteeAndApplications(
      MenteeRegistration menteeRegistration, MentorshipCycleEntity cycle) {
    var savedMentee = menteeRepository.create(menteeRegistration.mentee());
    saveMenteeRegistrations(menteeRegistration, cycle, savedMentee.getId());
    return savedMentee;
  }

  private void updateMenteeApplications(
      MenteeRegistration menteeRegistration, Long menteeId, MentorshipCycleEntity cycle) {
    validateRegistrationLimit(menteeId, cycle);
    saveMenteeRegistrations(menteeRegistration, cycle, menteeId);
  }

  private void saveMenteeRegistrations(
      MenteeRegistration menteeRegistration, MentorshipCycleEntity cycle, Long menteeId) {
    var applications = menteeRegistration.toApplications(cycle, menteeId);
    applications.forEach(registrationsRepo::create);
  }

  /**
   * Retrieves the MentorshipCycleEntity for the given mentorship type and cycle year. Validates
   * that the cycle is open and the mentorship type matches the current cycle.
   *
   * @param mentorshipType The type of mentorship for which the cycle is being retrieved.
   * @param cycleYear The year of the mentorship cycle.
   * @return The MentorshipCycleEntity corresponding to the specified type and year.
   * @throws MentorshipCycleClosedException If the mentorship cycle is closed.
   * @throws InvalidMentorshipTypeException If the mentorship type does not match the current cycle
   *     type.
   */
  private MentorshipCycleEntity getMentorshipCycle(
      final MentorshipType mentorshipType, final Year cycleYear) {
    final var openCycle = cycleRepository.findByYearAndType(cycleYear, mentorshipType);

    if (openCycle.isPresent()) {
      final MentorshipCycleEntity cycle = openCycle.get();

      // Only validate status if validation is enabled
      if (mentorshipConfig.getValidation().isEnabled()
          && cycle.getStatus() != CycleStatus.OPEN) {
        throw new MentorshipCycleClosedException(
            String.format(
                "Mentorship cycle for %s in %d is %s. Registration is not available.",
                mentorshipType, cycleYear.getValue(), cycle.getStatus()));
      }

      return cycle;
    }

    // Fallback to old mentorship service validation for backward compatibility
    final MentorshipCycle currentCycle = mentorshipService.getCurrentCycle();

    if (currentCycle == MentorshipService.CYCLE_CLOSED) {
      throw new MentorshipCycleClosedException(
          "Mentorship cycle is currently closed. Registration is not available.");
    }

    if (mentorshipType != currentCycle.cycle()) {
      throw new InvalidMentorshipTypeException(
          String.format(
              "Mentee mentorship type '%s' does not match current cycle type '%s'.",
              mentorshipType, currentCycle.cycle()));
    }

    return MentorshipCycleEntity.builder()
        .cycleYear(cycleYear)
        .mentorshipType(mentorshipType)
        .build();
  }

  /**
   * Validates that the mentee hasn't exceeded the registration limit for the cycle.
   *
   * @param menteeId The mentee ID
   * @param cycle The mentorship cycle
   * @throws MenteeRegistrationLimitExceededException if limit exceeded
   */
  private void validateRegistrationLimit(final Long menteeId, final MentorshipCycleEntity cycle) {
    final long registrationsCount =
        registrationsRepo.countMenteeApplications(menteeId, cycle.getCycleId());

    if (registrationsCount >= 5) {
      throw new MenteeRegistrationLimitExceededException(
          String.format(
              "Mentee %d has already reached the limit of 5 registrations for %s in %d",
              menteeId, cycle.getMentorshipType(), cycle.getCycleYear().getValue()));
    }
  }
}
