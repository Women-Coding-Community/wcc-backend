package com.wcc.platform.service;

import static com.wcc.platform.utils.MenteeApplicationTestBuilder.noMatchFound;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.pendingManualMatch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenteeWorkflowConfirmNoMatchTest {

  private static final String NO_MATCH_REASON = "No suitable mentor available";
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
        .updateStatus(99L, ApplicationStatus.NO_MATCH_FOUND, NO_MATCH_REASON);
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
