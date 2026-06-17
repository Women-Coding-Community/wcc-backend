package com.wcc.platform.service;

import static com.wcc.platform.utils.MenteeApplicationTestBuilder.baseBuilder;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.pending;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.pendingManualMatch;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.rejected;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.ApplicationNotFoundException;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenteeWorkflowRejectApplicationTest {

  private static final String REJECTION_REASON =
      "Application does not meet the eligibility criteria for this mentorship cycle";
  private static final Long MENTEE_ID = 10L;
  private static final Long CYCLE_ID = 5L;

  @Mock private MenteeApplicationRepository applicationRepository;
  @Mock private MentorRepository mentorRepository;
  @Mock private MentorshipMatchRepository matchRepository;
  @Mock private MentorshipCycleRepository cycleRepository;
  @Mock private MemberRepository memberRepository;
  @Mock private MentorshipService mentorshipService;
  @Mock private MentorshipNotificationService notificationService;

  @InjectMocks private MenteeWorkflowService service;

  @BeforeEach
  void setUp() {
    lenient().when(mentorshipService.getNotificationService()).thenReturn(notificationService);
    lenient().when(mentorshipService.getMentorRepository()).thenReturn(mentorRepository);
    lenient().when(mentorshipService.getMemberRepository()).thenReturn(memberRepository);
  }

  @Test
  @DisplayName("Given a PENDING application, when admin rejects, then status becomes REJECTED")
  void shouldRejectPendingApplicationAndUpdateStatusToRejected() {
    final MenteeApplication pendingApp = pending(1L, MENTEE_ID, 1);
    final MenteeApplication rejectedApp = rejected(1L, MENTEE_ID, 1);
    final MenteeApplication anotherPending = pending(2L, MENTEE_ID, 2);

    when(applicationRepository.findById(1L)).thenReturn(java.util.Optional.of(pendingApp));
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

    when(applicationRepository.findById(2L)).thenReturn(java.util.Optional.of(rejectedApp));

    assertThatThrownBy(() -> service.rejectApplication(2L, REJECTION_REASON))
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("No pending application with id 2");
  }

  @Test
  @DisplayName(
      "Given application not found, when admin rejects, then ApplicationNotFoundException thrown")
  void shouldThrowApplicationNotFoundExceptionWhenApplicationDoesNotExist() {
    when(applicationRepository.findById(99L)).thenReturn(java.util.Optional.empty());

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

    when(applicationRepository.findById(1L)).thenReturn(java.util.Optional.of(pendingApp));
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

    when(applicationRepository.findById(1L)).thenReturn(java.util.Optional.of(pendingApp));
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

    when(applicationRepository.findById(1L)).thenReturn(java.util.Optional.of(pendingApp));
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

    when(applicationRepository.findById(1L)).thenReturn(java.util.Optional.of(pendingApp));
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

    assertThat(captor.getValue().getStatus()).isEqualTo(ApplicationStatus.PENDING_MANUAL_MATCH);
  }
}
