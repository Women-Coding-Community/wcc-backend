package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service for admin operations on mentees (activate, reject, list pending). */
@Slf4j
@Service
@AllArgsConstructor
public class MenteeAdminService {

  private final MenteeRepository menteeRepository;
  private final MenteeApplicationRepository registrationsRepo;

  /**
   * Return all mentees with PENDING profile status awaiting admin review.
   *
   * @return List of pending mentees.
   */
  public List<Mentee> getPendingMentees() {
    final var pendingMentees = menteeRepository.findByStatus(ProfileStatus.PENDING);
    return pendingMentees == null ? List.of() : pendingMentees;
  }

  /**
   * Activate a mentee by setting their profile status to ACTIVE.
   *
   * @param menteeId the mentee ID
   * @return the updated mentee
   * @throws ContentNotFoundException if mentee not found
   */
  @Transactional
  public Mentee activateMentee(final Long menteeId) {
    menteeRepository
        .findById(menteeId)
        .orElseThrow(() -> new ContentNotFoundException("Mentee not found: " + menteeId));

    final Mentee updated = menteeRepository.updateProfileStatus(menteeId, ProfileStatus.ACTIVE);
    log.info("Mentee {} activated by admin", menteeId);
    return updated;
  }

  /**
   * Reject a mentee by setting their profile status to REJECTED and rejecting all pending
   * applications.
   *
   * @param menteeId the mentee ID
   * @param reason the reason for rejection
   * @return the updated mentee
   * @throws ContentNotFoundException if mentee not found
   */
  @Transactional
  public Mentee rejectMentee(final Long menteeId, final String reason) {
    menteeRepository
        .findById(menteeId)
        .orElseThrow(() -> new ContentNotFoundException("Mentee not found: " + menteeId));

    final List<MenteeApplication> pending = registrationsRepo.findPendingByMenteeId(menteeId);
    pending.forEach(
        app ->
            registrationsRepo.updateStatus(
                app.getApplicationId(), ApplicationStatus.REJECTED, reason));

    final Mentee updated = menteeRepository.updateProfileStatus(menteeId, ProfileStatus.REJECTED);
    log.info("Mentee {} rejected by admin, {} applications rejected", menteeId, pending.size());
    return updated;
  }
}
