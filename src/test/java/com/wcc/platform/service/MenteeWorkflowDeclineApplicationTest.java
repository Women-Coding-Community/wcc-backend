package com.wcc.platform.service;

import static com.wcc.platform.utils.MenteeApplicationTestBuilder.baseBuilder;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.declined;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.reviewing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.repository.MemberRepository;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenteeWorkflowDeclineApplicationTest {

  private static final String DECLINE_REASON = "Not a fit";
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
}
