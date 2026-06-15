package com.wcc.platform.service;

import static com.wcc.platform.utils.MenteeApplicationTestBuilder.pending;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.reviewing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.ApplicationNotFoundException;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenteeWorkflowApproveApplicationTest {

  private static final Long MENTEE_ID = 10L;
  private static final Long MENTOR_ID = 20L;
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
  @DisplayName(
      "Given a PENDING application, when admin approves, then status becomes MENTOR_REVIEWING")
  void shouldApprovePendingApplicationAndUpdateStatusToMentorReviewing() {
    final MenteeApplication pendingApp = pending(1L, MENTEE_ID, 1);
    final MenteeApplication approved = reviewing(1L, MENTEE_ID, 1);

    when(mentorRepository.findById(MENTOR_ID))
        .thenReturn(Optional.of(Mentor.mentorBuilder().build()));
    when(cycleRepository.findById(CYCLE_ID))
        .thenReturn(Optional.of(MentorshipCycleEntity.builder().build()));
    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pendingApp));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.MENTOR_REVIEWING, null))
        .thenReturn(approved);

    final MenteeApplication result = service.approveApplication(1L);

    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.MENTOR_REVIEWING);
  }

  @Test
  @DisplayName(
      "Given a PENDING application, when admin approves, "
          + "then sendApplicationUpdate and sendNewMenteesNotification are called")
  void shouldSendEmailNotificationsOnApproveApplication() {
    final MenteeApplication pendingApp = pending(1L, MENTEE_ID, 1);
    final MenteeApplication approved = reviewing(1L, MENTEE_ID, 1);
    final Mentor mentor = Mentor.mentorBuilder().build();
    final MentorshipCycleEntity cycle = MentorshipCycleEntity.builder().cycleId(CYCLE_ID).build();

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pendingApp));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.MENTOR_REVIEWING, null))
        .thenReturn(approved);
    when(mentorRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
    when(cycleRepository.findById(CYCLE_ID)).thenReturn(Optional.of(cycle));

    service.approveApplication(1L);

    verify(notificationService).sendApplicationUpdate(Optional.of(pendingApp), approved);
    verify(notificationService).sendNewMenteesNotification(mentor, cycle);
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
}
