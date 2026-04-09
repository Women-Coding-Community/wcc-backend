package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.ApplicationNotFoundException;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MenteeWorkflowServiceTest {

  private static final String REJECTION_REASON =
      "Application does not meet the eligibility criteria for this mentorship cycle";

  @Mock private MenteeApplicationRepository applicationRepository;
  @Mock private MenteeRepository menteeRepository;
  @Mock private MentorshipMatchRepository matchRepository;
  @Mock private MentorshipCycleRepository cycleRepository;

  private MenteeWorkflowService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service =
        new MenteeWorkflowService(
            applicationRepository, menteeRepository, matchRepository, cycleRepository);
  }

  @Test
  @DisplayName(
      "Given a PENDING application, when admin approves, then status becomes MENTOR_REVIEWING")
  void shouldApprovePendingApplicationAndUpdateStatusToMentorReviewing() {
    final MenteeApplication pending = pendingApplication(1L, 10L, 1);
    final MenteeApplication approved = reviewingApplication(1L, 10L);

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pending));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.MENTOR_REVIEWING, null))
        .thenReturn(approved);

    final MenteeApplication result = service.approveApplication(1L);

    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.MENTOR_REVIEWING);
  }

  @Test
  @DisplayName(
      "Given non-PENDING application, when admin approves, then ContentNotFoundException is thrown")
  void shouldThrowContentNotFoundExceptionWhenApprovedApplicationIsNotPending() {
    final MenteeApplication reviewing =
        MenteeApplication.builder()
            .applicationId(2L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.MENTOR_REVIEWING)
            .whyMentor("Great mentor")
            .build();

    when(applicationRepository.findById(2L)).thenReturn(Optional.of(reviewing));

    assertThatThrownBy(() -> service.approveApplication(2L))
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("No pending application with id 2");
  }

  @Test
  @DisplayName(
      "Given application not found, when admin approves, then ApplicationNotFoundException thrown")
  void shouldThrowApplicationNotFoundExceptionWhenApprovedApplicationDoesNotExist() {
    when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.approveApplication(99L))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessageContaining("Application not found with ID: 99");
  }

  @Test
  @DisplayName("Given a PENDING application, when admin rejects, then status becomes REJECTED")
  void shouldRejectPendingApplicationAndUpdateStatusToRejected() {
    final MenteeApplication pending = pendingApplication(1L, 10L, 1);
    final MenteeApplication rejected = rejectedApplication(1L, 10L);
    final MenteeApplication anotherPending = pendingApplication(2L, 10L, 2);

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pending));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.REJECTED, REJECTION_REASON))
        .thenReturn(rejected);
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(10L, 5L))
        .thenReturn(List.of(rejected, anotherPending));

    final MenteeApplication result = service.rejectApplication(1L, REJECTION_REASON);

    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    verify(applicationRepository, never()).create(any());
  }

  @Test
  @DisplayName(
      "Given non-PENDING application, when admin rejects, then ContentNotFoundException is thrown")
  void shouldThrowContentNotFoundExceptionWhenRejectedApplicationIsNotPending() {
    final MenteeApplication rejected =
        MenteeApplication.builder()
            .applicationId(2L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.REJECTED)
            .whyMentor("Great mentor")
            .build();

    when(applicationRepository.findById(2L)).thenReturn(Optional.of(rejected));

    assertThatThrownBy(() -> service.rejectApplication(2L, REJECTION_REASON))
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("No pending application with id 2");
  }

  @Test
  @DisplayName(
      "Given application not found, when admin rejects, then ApplicationNotFoundException thrown")
  void shouldThrowApplicationNotFoundExceptionWhenApplicationDoesNotExist() {
    when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.rejectApplication(99L, REJECTION_REASON))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessageContaining("Application not found with ID: 99");
  }

  private MenteeApplication pendingApplication(
      final Long applicationId, final Long menteeId, final int priority) {
    return MenteeApplication.builder()
        .applicationId(applicationId)
        .menteeId(menteeId)
        .mentorId(20L)
        .cycleId(5L)
        .priorityOrder(priority)
        .status(ApplicationStatus.PENDING)
        .whyMentor("Great mentor")
        .build();
  }

  private MenteeApplication reviewingApplication(final Long applicationId, final Long menteeId) {
    return MenteeApplication.builder()
        .applicationId(applicationId)
        .menteeId(menteeId)
        .mentorId(20L)
        .cycleId(5L)
        .priorityOrder(1)
        .status(ApplicationStatus.MENTOR_REVIEWING)
        .whyMentor("Great mentor")
        .build();
  }

  private MenteeApplication rejectedApplication(final Long applicationId, final Long menteeId) {
    return MenteeApplication.builder()
        .applicationId(applicationId)
        .menteeId(menteeId)
        .mentorId(20L)
        .cycleId(5L)
        .priorityOrder(1)
        .status(ApplicationStatus.REJECTED)
        .whyMentor("Great mentor")
        .build();
  }

  @Test
  @DisplayName(
      "Given all applications are non-forwardable after rejection, when rejecting, "
          + "then a PENDING_MANUAL_MATCH application is created")
  void shouldCreateManualMatchApplicationWhenAllApplicationsAreNonForwardable() {
    final MenteeApplication pending =
        MenteeApplication.builder()
            .applicationId(1L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.PENDING)
            .whyMentor("Great mentor")
            .build();

    final MenteeApplication rejected =
        MenteeApplication.builder()
            .applicationId(1L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.REJECTED)
            .whyMentor("Great mentor")
            .build();

    final MenteeApplication anotherRejected =
        MenteeApplication.builder()
            .applicationId(2L)
            .menteeId(10L)
            .mentorId(30L)
            .cycleId(5L)
            .priorityOrder(2)
            .status(ApplicationStatus.REJECTED)
            .whyMentor("Another mentor")
            .build();

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pending));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.REJECTED, REJECTION_REASON))
        .thenReturn(rejected);
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(10L, 5L))
        .thenReturn(List.of(rejected, anotherRejected));
    when(applicationRepository.findByMenteeCycleAndStatus(
            10L, 5L, ApplicationStatus.PENDING_MANUAL_MATCH))
        .thenReturn(Optional.empty());

    service.rejectApplication(1L, REJECTION_REASON);

    final ArgumentCaptor<MenteeApplication> captor =
        ArgumentCaptor.forClass(MenteeApplication.class);
    verify(applicationRepository).create(captor.capture());

    final MenteeApplication createdApp = captor.getValue();
    assertThat(createdApp.getMenteeId()).isEqualTo(10L);
    assertThat(createdApp.getMentorId()).isNull();
    assertThat(createdApp.getCycleId()).isEqualTo(5L);
    assertThat(createdApp.getPriorityOrder()).isNull();
    assertThat(createdApp.getStatus()).isEqualTo(ApplicationStatus.PENDING_MANUAL_MATCH);
  }

  @Test
  @DisplayName(
      "Given mentee has a MATCHED application (not non-forwardable), when rejecting other application, "
          + "then no PENDING_MANUAL_MATCH application is created")
  void shouldNotCreateManualMatchWhenMenteeHasMatchedApplication() {
    final MenteeApplication pending =
        MenteeApplication.builder()
            .applicationId(1L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(2)
            .status(ApplicationStatus.PENDING)
            .whyMentor("Great mentor")
            .build();

    final MenteeApplication rejected =
        MenteeApplication.builder()
            .applicationId(1L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(2)
            .status(ApplicationStatus.REJECTED)
            .whyMentor("Great mentor")
            .build();

    final MenteeApplication matched =
        MenteeApplication.builder()
            .applicationId(2L)
            .menteeId(10L)
            .mentorId(30L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.MATCHED)
            .whyMentor("Another mentor")
            .build();

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pending));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.REJECTED, REJECTION_REASON))
        .thenReturn(rejected);
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(10L, 5L))
        .thenReturn(List.of(matched, rejected));

    service.rejectApplication(1L, REJECTION_REASON);

    verify(applicationRepository, never()).create(any());
  }

  @Test
  @DisplayName(
      "Given a PENDING_MANUAL_MATCH already exists, when rejecting another application, "
          + "then no duplicate PENDING_MANUAL_MATCH is created")
  void shouldNotCreateDuplicateManualMatchApplication() {
    final MenteeApplication pending =
        MenteeApplication.builder()
            .applicationId(1L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.PENDING)
            .whyMentor("Great mentor")
            .build();

    final MenteeApplication rejected =
        MenteeApplication.builder()
            .applicationId(1L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.REJECTED)
            .whyMentor("Great mentor")
            .build();

    final MenteeApplication existingManualMatch =
        MenteeApplication.builder()
            .applicationId(99L)
            .menteeId(10L)
            .mentorId(null)
            .cycleId(5L)
            .priorityOrder(null)
            .status(ApplicationStatus.PENDING_MANUAL_MATCH)
            .build();

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pending));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.REJECTED, REJECTION_REASON))
        .thenReturn(rejected);
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(10L, 5L))
        .thenReturn(List.of(rejected, existingManualMatch));
    when(applicationRepository.findByMenteeCycleAndStatus(
            10L, 5L, ApplicationStatus.PENDING_MANUAL_MATCH))
        .thenReturn(Optional.of(existingManualMatch));

    service.rejectApplication(1L, REJECTION_REASON);

    verify(applicationRepository, never()).create(any());
  }

  @Test
  @DisplayName(
      "Given all applications include MENTOR_DECLINED status, when rejecting last pending, "
          + "then PENDING_MANUAL_MATCH application is created")
  void shouldCreateManualMatchWhenAllApplicationsIncludeMentorDeclined() {
    final MenteeApplication pending =
        MenteeApplication.builder()
            .applicationId(1L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(2)
            .status(ApplicationStatus.PENDING)
            .whyMentor("Great mentor")
            .build();

    final MenteeApplication rejected =
        MenteeApplication.builder()
            .applicationId(1L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(2)
            .status(ApplicationStatus.REJECTED)
            .whyMentor("Great mentor")
            .build();

    final MenteeApplication declined =
        MenteeApplication.builder()
            .applicationId(2L)
            .menteeId(10L)
            .mentorId(30L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.MENTOR_DECLINED)
            .whyMentor("Another mentor")
            .build();

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pending));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.REJECTED, REJECTION_REASON))
        .thenReturn(rejected);
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(10L, 5L))
        .thenReturn(List.of(declined, rejected));
    when(applicationRepository.findByMenteeCycleAndStatus(
            10L, 5L, ApplicationStatus.PENDING_MANUAL_MATCH))
        .thenReturn(Optional.empty());

    service.rejectApplication(1L, REJECTION_REASON);

    final ArgumentCaptor<MenteeApplication> captor =
        ArgumentCaptor.forClass(MenteeApplication.class);
    verify(applicationRepository).create(captor.capture());

    final MenteeApplication createdApp = captor.getValue();
    assertThat(createdApp.getStatus()).isEqualTo(ApplicationStatus.PENDING_MANUAL_MATCH);
  }
}
