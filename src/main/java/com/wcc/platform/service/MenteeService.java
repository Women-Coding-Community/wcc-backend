package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.*;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.MenteeRegistration;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/** Service for managing mentee-related operations. */
@Service
@AllArgsConstructor
public class MenteeService {
  private static final int MAX_MENTORS = 5;

  private final MentorshipCycleRepository cycleRepository;
  private final MenteeApplicationRepository registrationsRepo;
  private final MenteeRepository menteeRepository;
  private final MemberRepository memberRepository;
  private final MentorRepository mentorRepository;
  private final UserProvisionService userProvisionService;

  /**
   * Return all active mentees (status_id = 1).
   *
   * @return List of active mentees.
   */
  public List<Mentee> getAllMentees() {
    final var activeMentees = menteeRepository.findByStatus(ProfileStatus.ACTIVE);
    return activeMentees == null ? List.of() : activeMentees;
  }

  /**
   * Create a mentee registration for a mentorship cycle.
   *
   * @param request The registration details to process
   * @return The created or updated Mentee record.
   */
  public Mentee saveRegistration(final MenteeRegistration request) {
    validateMentors(request);

    final var cycle = getCurrentCycle();

    if (!request.mentorshipType().equals(cycle.getMentorshipType())) {
      throw new InvalidMentorshipTypeException(
          String.format(
              "Mentee mentorship type '%s' does not match current cycle type '%s'",
              request.mentorshipType(), cycle.getMentorshipType()));
    }

    final var menteeId = ensureMenteeId(request.mentee());
    if (menteeId != null) {
      request.mentee().setId(menteeId);
      final var filteredRegistrations = ignoreDuplicateApplications(request, cycle);
      final var registrationCount =
          registrationsRepo.countMenteeApplications(menteeId, cycle.getCycleId());

      validateRegistrationLimit(registrationCount);
      validateDuplicatedPriorities(
          menteeId, cycle.getCycleId(), filteredRegistrations.toApplications(cycle, menteeId));

      if (registrationCount != null && registrationCount > 0) {
        userProvisionService.provisionUserRole(
            menteeId, request.mentee().getEmail(), RoleType.MENTEE);
        return createMenteeRegistrations(filteredRegistrations, cycle);
      }
    }

    return saveRegistration(request, cycle);
  }

  private Mentee saveRegistration(
      final MenteeRegistration menteeRegistration, final MentorshipCycleEntity cycle) {
    final var mentee = menteeRegistration.mentee();

    final Mentee savedMentee = createOrUpdateMentee(mentee);
    final Long menteeId = savedMentee.getId();

    menteeRegistration.mentee().setId(menteeId);

    userProvisionService.provisionUserRole(menteeId, savedMentee.getEmail(), RoleType.MENTEE);

    validateDuplicatedPriorities(
        menteeId, cycle.getCycleId(), menteeRegistration.toApplications(cycle, menteeId));
    return createMenteeRegistrations(menteeRegistration, cycle);
  }

  private void validateDuplicatedPriorities(
      final Long menteeId, final Long cycleId, final List<MenteeApplication> applications) {
    final List<Integer> requestPriorities =
        applications.stream().map(MenteeApplication::getPriorityOrder).toList();

    if (requestPriorities.size() != requestPriorities.stream().distinct().count()) {
      throw new DuplicatedPriorityException("Priorities must be unique in the request");
    }

    final List<MenteeApplication> existingApplications =
        registrationsRepo.findByMenteeAndCycle(menteeId, cycleId);

    if (existingApplications.isEmpty()) {
      return;
    }

    final List<Integer> existingPriorities =
        existingApplications.stream().map(MenteeApplication::getPriorityOrder).toList();

    for (final Integer priority : requestPriorities) {
      if (existingPriorities.contains(priority)) {
        throw new DuplicatedPriorityException(
            String.format(
                "Mentee %d already has an application with priority %d in cycle %d",
                menteeId, priority, cycleId));
      }
    }
  }

  /**
   * Creates or updates a mentee, preserving existing member types if the member already exists.
   *
   * @param mentee The mentee to create or update
   * @return The saved mentee
   */
  private Mentee createOrUpdateMentee(final Mentee mentee) {
    if (mentee.getId() != null) {
      return handleMenteeWithId(mentee);
    }
    return handleMenteeWithoutId(mentee);
  }

  private Mentee handleMenteeWithId(final Mentee mentee) {
    if (menteeRepository.findById(mentee.getId()).isPresent()) {
      return menteeRepository.update(mentee.getId(), mentee);
    }

    final var existingMember = memberRepository.findById(mentee.getId()).orElse(null);
    if (existingMember != null) {
      mentee.setMemberTypes(mergeMemberTypes(existingMember.getMemberTypes()));
    } else {
      mentee.setMemberTypes(List.of(MemberType.MENTEE));
    }
    return menteeRepository.create(mentee);
  }

  private Mentee handleMenteeWithoutId(final Mentee mentee) {
    final var existingMember = memberRepository.findByEmail(mentee.getEmail()).orElse(null);

    if (existingMember == null) {
      mentee.setMemberTypes(List.of(MemberType.MENTEE));
      return menteeRepository.create(mentee);
    }

    mentee.setId(existingMember.getId());
    mentee.setMemberTypes(mergeMemberTypes(existingMember.getMemberTypes()));

    if (menteeRepository.findById(existingMember.getId()).isPresent()) {
      return menteeRepository.update(existingMember.getId(), mentee);
    }
    return menteeRepository.create(mentee);
  }

  /**
   * Merges existing member types with MENTEE type.
   *
   * @param existingMemberTypes The current member types
   * @return List of merged member types including MENTEE
   */
  private List<MemberType> mergeMemberTypes(final List<MemberType> existingMemberTypes) {
    final List<MemberType> mergedTypes = new ArrayList<>(existingMemberTypes);
    if (!mergedTypes.contains(MemberType.MENTEE)) {
      mergedTypes.add(MemberType.MENTEE);
    }
    return mergedTypes;
  }

  /** Check if the mentee exist by ID or Email. */
  private Long ensureMenteeId(final Mentee mentee) {
    if (mentee.getId() != null) {
      final var optMentee = menteeRepository.findById(mentee.getId());
      if (optMentee.isPresent()) {
        return mentee.getId();
      }
    }
    return memberRepository.findByEmail(mentee.getEmail()).map(Member::getId).orElse(null);
  }

  private Mentee createMenteeRegistrations(
      final MenteeRegistration menteeRegistration, final MentorshipCycleEntity cycle) {
    final var applications =
        menteeRegistration.toApplications(cycle, menteeRegistration.mentee().getId());
    applications.forEach(registrationsRepo::create);

    return menteeRepository.findById(menteeRegistration.mentee().getId()).orElseThrow();
  }

  /**
   * Filters out duplicate mentorship applications for a mentee within a given mentorship cycle.
   * Applications that reference mentors already associated with the mentee in the current cycle are
   * removed from the provided mentee registration.
   *
   * @param menteeRegistration The current registration details of the mentee, including planned
   *     applications.
   * @param cycle The mentorship cycle within which duplicates are identified and removed.
   * @return A new MenteeRegistration object with duplicate applications removed.
   */
  private MenteeRegistration ignoreDuplicateApplications(
      final MenteeRegistration menteeRegistration, final MentorshipCycleEntity cycle) {
    final var existingApplications =
        registrationsRepo.findByMenteeAndCycle(
            menteeRegistration.mentee().getId(), cycle.getCycleId());

    final var existingMentorIds =
        existingApplications.stream()
            .map(MenteeApplication::getMentorId)
            .collect(Collectors.toSet());

    final var filteredApplications =
        menteeRegistration.applications().stream()
            .filter(application -> !existingMentorIds.contains(application.mentorId()))
            .toList();

    return menteeRegistration.withApplications(filteredApplications);
  }

  private void validateMentors(final MenteeRegistration registrationRequest) {
    if (registrationRequest.applications() != null) {
      registrationRequest
          .applications()
          .forEach(
              application -> {
                if (mentorRepository.findById(application.mentorId()).isEmpty()) {
                  throw new MentorNotFoundException(application.mentorId());
                }
              });
    }
  }

  /**
   * Retrieves the MentorshipCycleEntity for the given mentorship type and cycle year. Validates
   * that the cycle is open and the mentorship type matches the current cycle.
   *
   * @return The MentorshipCycleEntity corresponding to the specified type and year.
   * @throws MentorshipCycleClosedException If the mentorship cycle is closed.
   */
  private MentorshipCycleEntity getCurrentCycle() {
    return cycleRepository
        .findOpenCycle()
        .orElseThrow(() -> new MentorshipCycleClosedException("Mentorship cycle is closed"));
  }

  /**
   * Validates that the mentee hasn't exceeded the registration limit for the cycle.
   *
   * @throws MenteeRegistrationLimitException if limit exceeded
   */
  private void validateRegistrationLimit(final Long registrationsCount) {
    if (registrationsCount != null && registrationsCount >= MAX_MENTORS) {
      throw new MenteeRegistrationLimitException(
          String.format(
              "Mentee has already reached the limit of 5 registrations for %d",
              registrationsCount));
    }
  }

  /**
   * Get all mentees having applications pending for manual match for the current cycle
   *
   * @return list of mentees
   */
  public List<Mentee> getMenteePendingManualMatch() {
    final List<MenteeApplication> pendingManualMatch =
        registrationsRepo.findByStatusAndCycle(
            ApplicationStatus.PENDING_MANUAL_MATCH, getCurrentCycle().getCycleId());

    if (pendingManualMatch == null || pendingManualMatch.isEmpty()) {
      return List.of();
    }

    return menteeRepository.findAllById(
        pendingManualMatch.stream()
            .map(MenteeApplication::getMenteeId)
            .collect(Collectors.toList()));
  }
}
