package com.wcc.platform.service;

import static com.wcc.platform.utils.MenteeApplicationTestBuilder.accepted;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.matched;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.reviewing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.ApplicationMenteeWorkflowException;
import com.wcc.platform.domain.exceptions.ApplicationNotFoundException;
import com.wcc.platform.domain.exceptions.MentorCapacityExceededException;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipMatch;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
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
class MenteeWorkflowAcceptApplicationTest {

  private static final Long MENTEE_ID = 10L;
  private static final Long MENTOR_ID = 20L;
  private static final Long CYCLE_ID = 5L;

  @Mock private MenteeApplicationRepository applicationRepository;
  @Mock private MentorshipMatchRepository matchRepository;
  @Mock private MentorshipCycleRepository cycleRepository;
  @Mock private MenteeRepository menteeRepository;
  @Mock private MentorRepository mentorRepository;
  @Mock private MentorshipService mentorshipService;
  @Mock private MentorshipNotificationService notificationService;
  @Mock private MentorshipMatchingService mentorshipMatchingService;

  @InjectMocks private MenteeWorkflowService service;

  @BeforeEach
  void setUp() {
    lenient().when(mentorshipService.getNotificationService()).thenReturn(notificationService);
    lenient().when(mentorshipService.getMentorRepository()).thenReturn(mentorRepository);
  }

  @Test
  @DisplayName(
      "Given a LONG_TERM cycle and MENTOR_REVIEWING application, "
          + "when mentor accepts, then status becomes MENTOR_ACCEPTED")
  void shouldSetStatusToMentorAcceptedForLongTermCycle() {
    final MenteeApplication reviewingApp = reviewing(1L, MENTEE_ID, 1);
    final MenteeApplication accepted = accepted(1L, MENTEE_ID, 1);
    final MentorshipCycleEntity longTermCycle =
        MentorshipCycleEntity.builder()
            .cycleId(CYCLE_ID)
            .mentorshipType(MentorshipType.LONG_TERM)
            .maxMenteesPerMentor(3)
            .build();

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(reviewingApp));
    when(cycleRepository.findById(CYCLE_ID)).thenReturn(Optional.of(longTermCycle));
    when(matchRepository.countActiveMenteesByMentorAndCycle(MENTOR_ID, CYCLE_ID)).thenReturn(0);
    when(applicationRepository.updateStatus(1L, ApplicationStatus.MENTOR_ACCEPTED, null))
        .thenReturn(accepted);

    final MenteeApplication result = service.acceptApplication(1L, null);

    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.MENTOR_ACCEPTED);
    verify(mentorshipMatchingService, never()).confirmMatch(any());
  }

  @Test
  @DisplayName(
      "Given an AD_HOC cycle and MENTOR_REVIEWING application, "
          + "when mentor accepts, then confirmMatch is called and MATCHED application is returned")
  void shouldDelegateToConfirmMatchForAdHocCycle() {
    final MenteeApplication reviewingApp = reviewing(1L, MENTEE_ID, 1);
    final MenteeApplication matchedApp = matched(1L, MENTEE_ID, 1);
    final MentorshipCycleEntity adHocCycle =
        MentorshipCycleEntity.builder()
            .cycleId(CYCLE_ID)
            .mentorshipType(MentorshipType.AD_HOC)
            .maxMenteesPerMentor(3)
            .build();

    when(applicationRepository.findById(1L))
        .thenReturn(Optional.of(reviewingApp))
        .thenReturn(Optional.of(matchedApp));
    when(cycleRepository.findById(CYCLE_ID)).thenReturn(Optional.of(adHocCycle));
    when(matchRepository.countActiveMenteesByMentorAndCycle(MENTOR_ID, CYCLE_ID)).thenReturn(0);
    when(mentorshipMatchingService.confirmMatch(1L)).thenReturn(MentorshipMatch.builder().build());

    final MenteeApplication result = service.acceptApplication(1L, null);

    verify(mentorshipMatchingService).confirmMatch(1L);
    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.MATCHED);
  }

  @Test
  @DisplayName(
      "Given mentor at full capacity, when mentor accepts, then MentorCapacityExceededException is thrown")
  void shouldThrowMentorCapacityExceededExceptionWhenMentorAtCapacity() {
    final MenteeApplication reviewingApp = reviewing(1L, MENTEE_ID, 1);
    final MentorshipCycleEntity cycle =
        MentorshipCycleEntity.builder()
            .cycleId(CYCLE_ID)
            .mentorshipType(MentorshipType.AD_HOC)
            .maxMenteesPerMentor(2)
            .build();

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(reviewingApp));
    when(cycleRepository.findById(CYCLE_ID)).thenReturn(Optional.of(cycle));
    when(matchRepository.countActiveMenteesByMentorAndCycle(MENTOR_ID, CYCLE_ID)).thenReturn(2);

    assertThatThrownBy(() -> service.acceptApplication(1L, null))
        .isInstanceOf(MentorCapacityExceededException.class);
  }

  @Test
  @DisplayName(
      "Given application in terminal state, when mentor accepts, "
          + "then ApplicationMenteeWorkflowException is thrown")
  void shouldThrowExceptionWhenApplicationIsInTerminalState() {
    final MenteeApplication matchedApp = matched(1L, MENTEE_ID, 1);

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(matchedApp));

    assertThatThrownBy(() -> service.acceptApplication(1L, null))
        .isInstanceOf(ApplicationMenteeWorkflowException.class)
        .hasMessageContaining("terminal state");
  }

  @Test
  @DisplayName(
      "Given application not found, when mentor accepts, then ApplicationNotFoundException is thrown")
  void shouldThrowApplicationNotFoundExceptionWhenApplicationDoesNotExist() {
    when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.acceptApplication(99L, null))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessageContaining("Application not found with ID: 99");
  }
}
