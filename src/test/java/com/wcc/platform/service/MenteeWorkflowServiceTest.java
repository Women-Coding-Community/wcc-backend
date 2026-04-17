package com.wcc.platform.service;

import static com.wcc.platform.utils.MenteeApplicationTestBuilder.baseBuilder;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.declined;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.noMatchFound;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.pending;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.pendingManualMatch;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.rejected;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.reviewing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.ApplicationNotFoundException;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.DuplicateApplicationException;
import com.wcc.platform.domain.exceptions.MentorCapacityExceededException;
import com.wcc.platform.domain.exceptions.MentorNotFoundException;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MentorRepository;
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
  private static final String DECLINE_REASON = "Not a fit";
  private static final String NO_MATCH_REASON = "No suitable mentor available";
  private static final String ASSIGNMENT_NOTES = "Manual assignment by admin";
  private static final Long MENTEE_ID = 10L;
  private static final Long MENTOR_ID = 20L;
  private static final Long CYCLE_ID = 5L;

  @Mock private MenteeApplicationRepository applicationRepository;
  @Mock private MentorRepository mentorRepository;
  @Mock private MentorshipMatchRepository matchRepository;
  @Mock private MentorshipCycleRepository cycleRepository;

  private MenteeWorkflowService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service =
        new MenteeWorkflowService(
            applicationRepository, mentorRepository, matchRepository, cycleRepository);
  }

  @Test
  @DisplayName(
      "Given a PENDING application, when admin approves, then status becomes MENTOR_REVIEWING")
  void shouldApprovePendingApplicationAndUpdateStatusToMentorReviewing() {
    final MenteeApplication pendingApp = pending(1L, MENTEE_ID, 1);
    final MenteeApplication approved = reviewing(1L, MENTEE_ID, 1);

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pendingApp));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.MENTOR_REVIEWING, null))
        .thenReturn(approved);

    final MenteeApplication result = service.approveApplication(1L);

    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.MENTOR_REVIEWING);
  }

  @Test
  @DisplayName(
      "Given non-PENDING application, when admin approves, then ContentNotFoundException is thrown")
  void shouldThrowContentNotFoundExceptionWhenApprovedApplicationIsNotPending() {
    final MenteeApplication reviewingApp = reviewing(2L, MENTEE_ID, 1);

    when(applicationRepository.findById(2L)).thenReturn(Optional.of(reviewingApp));

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
    final MenteeApplication pendingApp = pending(1L, MENTEE_ID, 1);
    final MenteeApplication rejectedApp = rejected(1L, MENTEE_ID, 1);
    final MenteeApplication anotherPending = pending(2L, MENTEE_ID, 2);

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pendingApp));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.REJECTED, REJECTION_REASON))
        .thenReturn(rejectedApp);
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(MENTEE_ID, CYCLE_ID))
        .thenReturn(List.of(rejectedApp, anotherPending));

    final MenteeApplication result = service.rejectApplication(1L, REJECTION_REASON);

    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    verify(applicationRepository, never()).create(any());
  }

  @Test
  @DisplayName(
      "Given non-PENDING application, when admin rejects, then ContentNotFoundException is thrown")
  void shouldThrowContentNotFoundExceptionWhenRejectedApplicationIsNotPending() {
    final MenteeApplication rejectedApp = rejected(2L, MENTEE_ID, 1);

    when(applicationRepository.findById(2L)).thenReturn(Optional.of(rejectedApp));

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

  @Test
  @DisplayName(
      "Given all applications are non-forwardable after rejection, when rejecting, "
          + "then a PENDING_MANUAL_MATCH application is created")
  void shouldCreateManualMatchApplicationWhenAllApplicationsAreNonForwardable() {
    final MenteeApplication pendingApp = pending(1L, MENTEE_ID, 1);
    final MenteeApplication rejectedApp = rejected(1L, MENTEE_ID, 1);
    final MenteeApplication anotherRejected =
        baseBuilder(2L, MENTEE_ID, 30L, 2).status(ApplicationStatus.REJECTED).build();

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pendingApp));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.REJECTED, REJECTION_REASON))
        .thenReturn(rejectedApp);
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(MENTEE_ID, CYCLE_ID))
        .thenReturn(List.of(rejectedApp, anotherRejected));
    when(applicationRepository.findByMenteeCycleAndStatusOrderByPriority(
            MENTEE_ID, CYCLE_ID, ApplicationStatus.PENDING_MANUAL_MATCH))
        .thenReturn(List.of());

    service.rejectApplication(1L, REJECTION_REASON);

    final ArgumentCaptor<MenteeApplication> captor =
        ArgumentCaptor.forClass(MenteeApplication.class);
    verify(applicationRepository).create(captor.capture());

    final MenteeApplication createdApp = captor.getValue();
    assertThat(createdApp.getMenteeId()).isEqualTo(MENTEE_ID);
    assertThat(createdApp.getMentorId()).isNull();
    assertThat(createdApp.getCycleId()).isEqualTo(CYCLE_ID);
    assertThat(createdApp.getPriorityOrder()).isNull();
    assertThat(createdApp.getStatus()).isEqualTo(ApplicationStatus.PENDING_MANUAL_MATCH);
  }

  @Test
  @DisplayName(
      "Given mentee has a MATCHED application (not non-forwardable), when rejecting other application, "
          + "then no PENDING_MANUAL_MATCH application is created")
  void shouldNotCreateManualMatchWhenMenteeHasMatchedApplication() {
    final MenteeApplication pendingApp = pending(1L, MENTEE_ID, 2);
    final MenteeApplication rejectedApp = rejected(1L, MENTEE_ID, 2);
    final MenteeApplication matchedApp =
        baseBuilder(2L, MENTEE_ID, 30L, 1).status(ApplicationStatus.MATCHED).build();

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pendingApp));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.REJECTED, REJECTION_REASON))
        .thenReturn(rejectedApp);
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(MENTEE_ID, CYCLE_ID))
        .thenReturn(List.of(matchedApp, rejectedApp));

    service.rejectApplication(1L, REJECTION_REASON);

    verify(applicationRepository, never()).create(any());
  }

  @Test
  @DisplayName(
      "Given a PENDING_MANUAL_MATCH already exists, when rejecting another application, "
          + "then no duplicate PENDING_MANUAL_MATCH is created")
  void shouldNotCreateDuplicateManualMatchApplication() {
    final MenteeApplication pendingApp = pending(1L, MENTEE_ID, 1);
    final MenteeApplication rejectedApp = rejected(1L, MENTEE_ID, 1);
    final MenteeApplication existingManualMatch = pendingManualMatch(99L, MENTEE_ID);

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pendingApp));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.REJECTED, REJECTION_REASON))
        .thenReturn(rejectedApp);
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(MENTEE_ID, CYCLE_ID))
        .thenReturn(List.of(rejectedApp, existingManualMatch));
    when(applicationRepository.findByMenteeCycleAndStatusOrderByPriority(
            MENTEE_ID, CYCLE_ID, ApplicationStatus.PENDING_MANUAL_MATCH))
        .thenReturn(List.of(existingManualMatch));

    service.rejectApplication(1L, REJECTION_REASON);

    verify(applicationRepository, never()).create(any());
  }

  @Test
  @DisplayName(
      "Given all applications include MENTOR_DECLINED status, when rejecting last pending, "
          + "then PENDING_MANUAL_MATCH application is created")
  void shouldCreateManualMatchWhenAllApplicationsIncludeMentorDeclined() {
    final MenteeApplication pendingApp = pending(1L, MENTEE_ID, 2);
    final MenteeApplication rejectedApp = rejected(1L, MENTEE_ID, 2);
    final MenteeApplication declinedApp =
        baseBuilder(2L, MENTEE_ID, 30L, 1).status(ApplicationStatus.MENTOR_DECLINED).build();

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pendingApp));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.REJECTED, REJECTION_REASON))
        .thenReturn(rejectedApp);
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(MENTEE_ID, CYCLE_ID))
        .thenReturn(List.of(declinedApp, rejectedApp));
    when(applicationRepository.findByMenteeCycleAndStatusOrderByPriority(
            MENTEE_ID, CYCLE_ID, ApplicationStatus.PENDING_MANUAL_MATCH))
        .thenReturn(List.of());

    service.rejectApplication(1L, REJECTION_REASON);

    final ArgumentCaptor<MenteeApplication> captor =
        ArgumentCaptor.forClass(MenteeApplication.class);
    verify(applicationRepository).create(captor.capture());

    final MenteeApplication createdApp = captor.getValue();
    assertThat(createdApp.getStatus()).isEqualTo(ApplicationStatus.PENDING_MANUAL_MATCH);
  }

  @Test
  @DisplayName(
      "Given all applications are non-forwardable after mentor decline, when declining, "
          + "then a PENDING_MANUAL_MATCH application is created")
  void shouldCreateManualMatchApplicationWhenAllApplicationsAreNonForwardableAfterDecline() {
    final MenteeApplication reviewingApp = reviewing(1L, MENTEE_ID, 1);
    final MenteeApplication declinedApp = declined(1L, MENTEE_ID, 1);
    final MenteeApplication anotherDeclined =
        baseBuilder(2L, MENTEE_ID, 30L, 2).status(ApplicationStatus.MENTOR_DECLINED).build();

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(reviewingApp));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.MENTOR_DECLINED, DECLINE_REASON))
        .thenReturn(declinedApp);
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(MENTEE_ID, CYCLE_ID))
        .thenReturn(List.of(declinedApp, anotherDeclined));
    when(applicationRepository.findByMenteeCycleAndStatusOrderByPriority(
            MENTEE_ID, CYCLE_ID, ApplicationStatus.PENDING_MANUAL_MATCH))
        .thenReturn(List.of());

    service.declineApplication(1L, DECLINE_REASON);

    final ArgumentCaptor<MenteeApplication> captor =
        ArgumentCaptor.forClass(MenteeApplication.class);
    verify(applicationRepository).create(captor.capture());

    final MenteeApplication createdApp = captor.getValue();
    assertThat(createdApp.getMenteeId()).isEqualTo(MENTEE_ID);
    assertThat(createdApp.getMentorId()).isNull();
    assertThat(createdApp.getCycleId()).isEqualTo(CYCLE_ID);
    assertThat(createdApp.getPriorityOrder()).isNull();
    assertThat(createdApp.getStatus()).isEqualTo(ApplicationStatus.PENDING_MANUAL_MATCH);
  }

  @Test
  @DisplayName(
      "Given mentee has a MATCHED application, when mentor declines another application, "
          + "then no PENDING_MANUAL_MATCH application is created")
  void shouldNotCreateManualMatchWhenMenteeHasMatchedApplicationAfterDecline() {
    final MenteeApplication reviewingApp = reviewing(1L, MENTEE_ID, 2);
    final MenteeApplication declinedApp = declined(1L, MENTEE_ID, 2);
    final MenteeApplication matchedApp =
        baseBuilder(2L, MENTEE_ID, 30L, 1).status(ApplicationStatus.MATCHED).build();

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(reviewingApp));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.MENTOR_DECLINED, DECLINE_REASON))
        .thenReturn(declinedApp);
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(MENTEE_ID, CYCLE_ID))
        .thenReturn(List.of(matchedApp, declinedApp));

    service.declineApplication(1L, DECLINE_REASON);

    verify(applicationRepository, never()).create(any());
  }

  @Test
  @DisplayName(
      "Given valid inputs and PENDING_MANUAL_MATCH exists, when assigning mentor, "
          + "then new application is created with PENDING status")
  void shouldAssignMentorSuccessfully() {
    final MenteeApplication manualMatchApp = pendingManualMatch(99L, MENTEE_ID);
    final MenteeApplication createdApp =
        baseBuilder(100L, MENTEE_ID, MENTOR_ID, 0).status(ApplicationStatus.PENDING).build();
    final MentorshipCycleEntity cycle =
        MentorshipCycleEntity.builder().cycleId(CYCLE_ID).maxMenteesPerMentor(3).build();

    when(mentorRepository.findById(MENTOR_ID))
        .thenReturn(Optional.of(Mentor.mentorBuilder().build()));
    when(applicationRepository.findByMenteeMentorCycle(MENTEE_ID, MENTOR_ID, CYCLE_ID))
        .thenReturn(Optional.empty());
    when(cycleRepository.findById(CYCLE_ID)).thenReturn(Optional.of(cycle));
    when(matchRepository.countActiveMenteesByMentorAndCycle(MENTOR_ID, CYCLE_ID)).thenReturn(0);
    when(applicationRepository.findByMenteeCycleAndStatusOrderByPriority(
            MENTEE_ID, CYCLE_ID, ApplicationStatus.PENDING_MANUAL_MATCH))
        .thenReturn(List.of(manualMatchApp));
    when(applicationRepository.create(any(MenteeApplication.class))).thenReturn(createdApp);

    final MenteeApplication result =
        service.assignMentor(MENTEE_ID, CYCLE_ID, MENTOR_ID, ASSIGNMENT_NOTES);

    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.PENDING);
    verify(applicationRepository)
        .updateStatus(99L, ApplicationStatus.REJECTED, "Manually assigned mentor");

    final ArgumentCaptor<MenteeApplication> captor =
        ArgumentCaptor.forClass(MenteeApplication.class);
    verify(applicationRepository).create(captor.capture());
    final MenteeApplication created = captor.getValue();
    assertThat(created.getMenteeId()).isEqualTo(MENTEE_ID);
    assertThat(created.getMentorId()).isEqualTo(MENTOR_ID);
    assertThat(created.getCycleId()).isEqualTo(CYCLE_ID);
    assertThat(created.getStatus()).isEqualTo(ApplicationStatus.PENDING);
  }

  @Test
  @DisplayName(
      "Given mentor does not exist, when assigning mentor, then MentorNotFoundException is thrown")
  void shouldThrowMentorNotFoundExceptionWhenMentorDoesNotExist() {
    when(mentorRepository.findById(MENTOR_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.assignMentor(MENTEE_ID, CYCLE_ID, MENTOR_ID, ASSIGNMENT_NOTES))
        .isInstanceOf(MentorNotFoundException.class)
        .hasMessageContaining(String.valueOf(MENTOR_ID));
  }

  @Test
  @DisplayName(
      "Given existing application between mentee and mentor, when assigning mentor, "
          + "then DuplicateApplicationException is thrown")
  void shouldThrowExceptionWhenApplicationAlreadyExists() {
    final MenteeApplication existingApp = rejected(50L, MENTEE_ID, 1);

    when(mentorRepository.findById(MENTOR_ID))
        .thenReturn(Optional.of(Mentor.mentorBuilder().build()));
    when(applicationRepository.findByMenteeMentorCycle(MENTEE_ID, MENTOR_ID, CYCLE_ID))
        .thenReturn(Optional.of(existingApp));

    assertThatThrownBy(() -> service.assignMentor(MENTEE_ID, CYCLE_ID, MENTOR_ID, ASSIGNMENT_NOTES))
        .isInstanceOf(DuplicateApplicationException.class)
        .hasMessageContaining("application already exists");
  }

  @Test
  @DisplayName(
      "Given no PENDING_MANUAL_MATCH application, when assigning mentor, "
          + "then ContentNotFoundException is thrown")
  void shouldThrowContentNotFoundWhenNoPendingManualMatchExists() {
    final MentorshipCycleEntity cycle =
        MentorshipCycleEntity.builder().cycleId(CYCLE_ID).maxMenteesPerMentor(3).build();

    when(mentorRepository.findById(MENTOR_ID))
        .thenReturn(Optional.of(Mentor.mentorBuilder().build()));
    when(applicationRepository.findByMenteeMentorCycle(MENTEE_ID, MENTOR_ID, CYCLE_ID))
        .thenReturn(Optional.empty());
    when(cycleRepository.findById(CYCLE_ID)).thenReturn(Optional.of(cycle));
    when(matchRepository.countActiveMenteesByMentorAndCycle(MENTOR_ID, CYCLE_ID)).thenReturn(0);
    when(applicationRepository.findByMenteeCycleAndStatusOrderByPriority(
            MENTEE_ID, CYCLE_ID, ApplicationStatus.PENDING_MANUAL_MATCH))
        .thenReturn(List.of());

    assertThatThrownBy(() -> service.assignMentor(MENTEE_ID, CYCLE_ID, MENTOR_ID, ASSIGNMENT_NOTES))
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("PENDING_MANUAL_MATCH");
  }

  @Test
  @DisplayName(
      "Given mentor is at capacity, when assigning mentor, "
          + "then MentorCapacityExceededException is thrown")
  void shouldThrowMentorCapacityExceededWhenMentorAtCapacity() {
    final MentorshipCycleEntity cycle =
        MentorshipCycleEntity.builder().cycleId(CYCLE_ID).maxMenteesPerMentor(2).build();

    when(mentorRepository.findById(MENTOR_ID))
        .thenReturn(Optional.of(Mentor.mentorBuilder().build()));
    when(applicationRepository.findByMenteeMentorCycle(MENTEE_ID, MENTOR_ID, CYCLE_ID))
        .thenReturn(Optional.empty());
    when(cycleRepository.findById(CYCLE_ID)).thenReturn(Optional.of(cycle));
    when(matchRepository.countActiveMenteesByMentorAndCycle(MENTOR_ID, CYCLE_ID)).thenReturn(2);

    assertThatThrownBy(() -> service.assignMentor(MENTEE_ID, CYCLE_ID, MENTOR_ID, ASSIGNMENT_NOTES))
        .isInstanceOf(MentorCapacityExceededException.class)
        .hasMessageContaining("maximum capacity");
  }

  // ==================== confirmNoMatch tests ====================

  @Test
  @DisplayName(
      "Given PENDING_MANUAL_MATCH exists, when confirming no match, "
          + "then status is updated to NO_MATCH_FOUND")
  void shouldConfirmNoMatchSuccessfully() {
    final MenteeApplication manualMatchApp = pendingManualMatch(99L, MENTEE_ID);
    final MenteeApplication updatedApp = noMatchFound(99L, MENTEE_ID);

    when(applicationRepository.findByMenteeCycleAndStatusOrderByPriority(
            MENTEE_ID, CYCLE_ID, ApplicationStatus.PENDING_MANUAL_MATCH))
        .thenReturn(List.of(manualMatchApp));
    when(applicationRepository.updateStatus(99L, ApplicationStatus.NO_MATCH_FOUND, NO_MATCH_REASON))
        .thenReturn(updatedApp);

    final MenteeApplication result = service.confirmNoMatch(MENTEE_ID, CYCLE_ID, NO_MATCH_REASON);

    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.NO_MATCH_FOUND);
    verify(applicationRepository)
        .updateStatus(eq(99L), eq(ApplicationStatus.NO_MATCH_FOUND), eq(NO_MATCH_REASON));
  }

  @Test
  @DisplayName(
      "Given no PENDING_MANUAL_MATCH application, when confirming no match, "
          + "then ContentNotFoundException is thrown")
  void shouldThrowContentNotFoundWhenNoPendingManualMatchExistsForNoMatch() {
    when(applicationRepository.findByMenteeCycleAndStatusOrderByPriority(
            MENTEE_ID, CYCLE_ID, ApplicationStatus.PENDING_MANUAL_MATCH))
        .thenReturn(List.of());

    assertThatThrownBy(() -> service.confirmNoMatch(MENTEE_ID, CYCLE_ID, NO_MATCH_REASON))
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("PENDING_MANUAL_MATCH");
  }
}
