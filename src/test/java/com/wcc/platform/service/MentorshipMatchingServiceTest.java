package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupMenteeFactories.createMenteeTest;
import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.baseBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.ApplicationNotFoundException;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MatchStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipMatch;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorshipMatchingServiceTest {

  private static final Long APPLICATION_ID = 1L;
  private static final Long MENTOR_ID = 20L;
  private static final Long MENTEE_ID = 10L;
  private static final Long CYCLE_ID = 5L;
  private static final String GOOGLE_MEET_LINK = "https://meet.google.com/abc-xyz";

  @Mock private MentorshipMatchRepository matchRepository;
  @Mock private MenteeApplicationRepository applicationRepository;
  @Mock private MentorshipCycleRepository cycleRepository;
  @Mock private MenteeRepository menteeRepository;
  @Mock private MentorshipService mentorshipService;
  @Mock private MentorRepository mentorRepository;
  @Mock private MentorshipNotificationService notificationService;

  @InjectMocks private MentorshipMatchingService service;

  @BeforeEach
  void setUp() {
    lenient().when(mentorshipService.getNotificationService()).thenReturn(notificationService);
    lenient().when(mentorshipService.getMentorRepository()).thenReturn(mentorRepository);
  }

  @Test
  @DisplayName(
      "Given a MENTOR_ACCEPTED application, when confirming match with google meet link,"
          + " then creates match with link and sends pairing email to mentor and mentee")
  void shouldConfirmMatchAndSendPairingEmail() {
    final var application =
        baseBuilder(APPLICATION_ID, MENTEE_ID, MENTOR_ID, 1)
            .status(ApplicationStatus.MENTOR_ACCEPTED)
            .build();
    final var cycle =
        MentorshipCycleEntity.builder()
            .cycleId(CYCLE_ID)
            .maxMenteesPerMentor(3)
            .cycleYear(Year.of(2025))
            .cycleEndDate(LocalDate.of(2025, 10, 31))
            .build();
    final var mentor = createMentorTest(MENTOR_ID, "Jane Mentor", "mentor@test.com");
    final var mentee = createMenteeTest(MENTEE_ID, "Alice Mentee", "mentee@test.com");
    final var createdMatch =
        MentorshipMatch.builder()
            .matchId(100L)
            .mentorId(MENTOR_ID)
            .menteeId(MENTEE_ID)
            .cycleId(CYCLE_ID)
            .applicationId(APPLICATION_ID)
            .status(MatchStatus.ACTIVE)
            .startDate(LocalDate.now())
            .googleMeetLink(GOOGLE_MEET_LINK)
            .build();

    when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.of(application));
    when(cycleRepository.findById(CYCLE_ID)).thenReturn(Optional.of(cycle));
    when(matchRepository.countActiveMenteesByMentorAndCycle(MENTOR_ID, CYCLE_ID)).thenReturn(0);
    when(matchRepository.isMenteeMatchedInCycle(MENTEE_ID, CYCLE_ID)).thenReturn(false);
    when(matchRepository.create(any(MentorshipMatch.class))).thenReturn(createdMatch);
    when(mentorRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
    when(menteeRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(MENTEE_ID, CYCLE_ID))
        .thenReturn(List.of(application));

    final MentorshipMatch result = service.confirmMatch(APPLICATION_ID, GOOGLE_MEET_LINK);

    assertThat(result.getStatus()).isEqualTo(MatchStatus.ACTIVE);
    assertThat(result.getGoogleMeetLink()).isEqualTo(GOOGLE_MEET_LINK);

    verify(applicationRepository)
        .updateStatus(APPLICATION_ID, ApplicationStatus.MATCHED, "Match confirmed by mentorship team");
    verify(notificationService).sendConfirmLongTermPairing(mentor, mentee, 2025, GOOGLE_MEET_LINK);
  }

  @Test
  @DisplayName(
      "Given application not found, when confirming match, then ApplicationNotFoundException is thrown")
  void shouldThrowApplicationNotFoundExceptionWhenApplicationDoesNotExist() {
    when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.confirmMatch(APPLICATION_ID, GOOGLE_MEET_LINK))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessageContaining(String.valueOf(APPLICATION_ID));
  }

  @Test
  @DisplayName(
      "Given application not in MENTOR_ACCEPTED status, when confirming match,"
          + " then IllegalStateException is thrown")
  void shouldThrowIllegalStateExceptionWhenApplicationNotInAcceptedStatus() {
    final var pendingApp =
        baseBuilder(APPLICATION_ID, MENTEE_ID, MENTOR_ID, 1)
            .status(ApplicationStatus.PENDING)
            .build();
    when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.of(pendingApp));

    assertThatThrownBy(() -> service.confirmMatch(APPLICATION_ID, GOOGLE_MEET_LINK))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("MENTOR_ACCEPTED");
  }
}
