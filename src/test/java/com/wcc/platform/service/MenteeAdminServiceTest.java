package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMenteeFactories.createMenteeTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MenteeAdminServiceTest {

  @Mock private MenteeRepository menteeRepository;
  @Mock private MenteeApplicationRepository registrationsRepo;

  private MenteeAdminService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new MenteeAdminService(menteeRepository, registrationsRepo);
  }

  @Test
  @DisplayName(
      "Given pending mentees exist, when getting pending mentees, then should return pending mentees")
  void shouldReturnPendingMentees() {
    final var pendingMentee = createMenteeTest(5L, "Pending Mentee", "pending@wcc.com");
    when(menteeRepository.findByStatus(ProfileStatus.PENDING)).thenReturn(List.of(pendingMentee));

    final var result = service.getPendingMentees();

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getId()).isEqualTo(5L);
    verify(menteeRepository).findByStatus(ProfileStatus.PENDING);
  }

  @Test
  @DisplayName(
      "Given no pending mentees, when getting pending mentees, then should return empty list")
  void shouldReturnEmptyListWhenNoPendingMentees() {
    when(menteeRepository.findByStatus(ProfileStatus.PENDING)).thenReturn(List.of());

    final var result = service.getPendingMentees();

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Given existing mentee, when activating mentee, then should set status to ACTIVE")
  void shouldActivateMenteeSuccessfully() {
    final var activeMentee = createMenteeTest(10L, "Active Mentee", "active@wcc.com");
    when(menteeRepository.findById(10L)).thenReturn(Optional.of(activeMentee));
    when(menteeRepository.updateProfileStatus(10L, ProfileStatus.ACTIVE)).thenReturn(activeMentee);

    final var result = service.activateMentee(10L);

    assertThat(result).isEqualTo(activeMentee);
    verify(menteeRepository).updateProfileStatus(10L, ProfileStatus.ACTIVE);
  }

  @Test
  @DisplayName(
      "Given mentee not found, when activating mentee, then should throw ContentNotFoundException")
  void shouldThrowContentNotFoundExceptionWhenActivatingNonExistentMentee() {
    when(menteeRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ContentNotFoundException.class, () -> service.activateMentee(99L));
  }

  @Test
  @DisplayName(
      "Given existing mentee with pending applications, when rejecting mentee, "
          + "then should set status to REJECTED and reject all pending applications")
  void shouldRejectMenteeAndAllPendingApplications() {
    final var rejectedMentee = createMenteeTest(10L, "Rejected Mentee", "rejected@wcc.com");
    final var pendingApp =
        MenteeApplication.builder()
            .applicationId(1L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.PENDING)
            .build();
    final String reason = "Does not meet criteria for this mentorship cycle at this time";

    when(menteeRepository.findById(10L)).thenReturn(Optional.of(rejectedMentee));
    when(registrationsRepo.findPendingByMenteeId(10L)).thenReturn(List.of(pendingApp));
    when(menteeRepository.updateProfileStatus(10L, ProfileStatus.REJECTED))
        .thenReturn(rejectedMentee);

    final var result = service.rejectMentee(10L, reason);

    assertThat(result).isEqualTo(rejectedMentee);
    verify(registrationsRepo).updateStatus(1L, ApplicationStatus.REJECTED, reason);
    verify(menteeRepository).updateProfileStatus(10L, ProfileStatus.REJECTED);
  }

  @Test
  @DisplayName(
      "Given mentee not found, when rejecting mentee, then should throw ContentNotFoundException")
  void shouldThrowContentNotFoundExceptionWhenRejectingNonExistentMentee() {
    when(menteeRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(
        ContentNotFoundException.class,
        () -> service.rejectMentee(99L, "Some reason for rejection that is long enough"));
  }
}
