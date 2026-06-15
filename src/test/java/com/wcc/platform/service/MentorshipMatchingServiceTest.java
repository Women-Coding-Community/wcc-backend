package com.wcc.platform.service;

import static com.wcc.platform.utils.MenteeApplicationTestBuilder.baseBuilder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MatchStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipMatch;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.time.LocalDate;
import java.time.Month;
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

  private static final Long MENTEE_ID = 10L;
  private static final Long MENTOR_ID = 20L;
  private static final Long CYCLE_ID = 5L;
  private static final Long APPLICATION_ID = 1L;
  private static final Long MATCH_ID = 100L;
  private static final LocalDate FIXED_DATE = LocalDate.of(2026, Month.JANUARY, 1);

  @Mock private MentorshipMatchRepository matchRepository;
  @Mock private MenteeApplicationRepository applicationRepository;
  @Mock private MentorshipCycleRepository cycleRepository;
  @Mock private MentorshipService mentorshipService;
  @Mock private MenteeRepository menteeRepository;
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
      "Given a MENTOR_ACCEPTED application, when confirmMatch is called, "
          + "then sendMatchUpdate is called with the created match")
  void shouldSendMatchUpdateOnConfirmMatch() {
    final MenteeApplication application =
        baseBuilder(APPLICATION_ID, MENTEE_ID, MENTOR_ID, 1)
            .status(ApplicationStatus.MENTOR_ACCEPTED)
            .build();
    final MentorshipCycleEntity cycle =
        MentorshipCycleEntity.builder().cycleId(CYCLE_ID).maxMenteesPerMentor(3).build();
    final MentorshipMatch createdMatch =
        MentorshipMatch.builder()
            .matchId(MATCH_ID)
            .mentorId(MENTOR_ID)
            .menteeId(MENTEE_ID)
            .cycleId(CYCLE_ID)
            .applicationId(APPLICATION_ID)
            .status(MatchStatus.ACTIVE)
            .startDate(FIXED_DATE)
            .build();
    final Mentee mentee =
        Mentee.menteeBuilder().id(MENTEE_ID).spokenLanguages(List.of("ENGLISH")).build();

    when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.of(application));
    when(cycleRepository.findById(CYCLE_ID)).thenReturn(Optional.of(cycle));
    when(matchRepository.countActiveMenteesByMentorAndCycle(MENTOR_ID, CYCLE_ID)).thenReturn(0);
    when(matchRepository.isMenteeMatchedInCycle(MENTEE_ID, CYCLE_ID)).thenReturn(false);
    when(matchRepository.create(any(MentorshipMatch.class))).thenReturn(createdMatch);
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(MENTEE_ID, CYCLE_ID))
        .thenReturn(List.of(application));
    when(mentorRepository.findById(MENTOR_ID))
        .thenReturn(Optional.of(Mentor.mentorBuilder().build()));
    when(menteeRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));

    service.confirmMatch(APPLICATION_ID);

    verify(notificationService).sendMatchUpdate(Optional.empty(), createdMatch);
  }

  @Test
  @DisplayName(
      "Given a MENTOR_ACCEPTED application, when confirmMatch is called, "
          + "then sendPairingConfirmation is called with mentor, mentee and cycle")
  void shouldSendPairingConfirmationOnConfirmMatch() {
    final MenteeApplication application =
        baseBuilder(APPLICATION_ID, MENTEE_ID, MENTOR_ID, 1)
            .status(ApplicationStatus.MENTOR_ACCEPTED)
            .build();
    final MentorshipCycleEntity cycle =
        MentorshipCycleEntity.builder().cycleId(CYCLE_ID).maxMenteesPerMentor(3).build();
    final Mentor mentor = Mentor.mentorBuilder().build();
    final Mentee mentee =
        Mentee.menteeBuilder().id(MENTEE_ID).spokenLanguages(List.of("ENGLISH")).build();

    when(applicationRepository.findById(APPLICATION_ID)).thenReturn(Optional.of(application));
    when(cycleRepository.findById(CYCLE_ID)).thenReturn(Optional.of(cycle));
    when(matchRepository.countActiveMenteesByMentorAndCycle(MENTOR_ID, CYCLE_ID)).thenReturn(0);
    when(matchRepository.isMenteeMatchedInCycle(MENTEE_ID, CYCLE_ID)).thenReturn(false);
    when(matchRepository.create(any(MentorshipMatch.class)))
        .thenReturn(
            MentorshipMatch.builder()
                .matchId(MATCH_ID)
                .mentorId(MENTOR_ID)
                .menteeId(MENTEE_ID)
                .cycleId(CYCLE_ID)
                .applicationId(APPLICATION_ID)
                .status(MatchStatus.ACTIVE)
                .startDate(FIXED_DATE)
                .build());
    when(applicationRepository.findByMenteeAndCycleOrderByPriority(MENTEE_ID, CYCLE_ID))
        .thenReturn(List.of(application));
    when(mentorRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
    when(menteeRepository.findById(MENTEE_ID)).thenReturn(Optional.of(mentee));

    service.confirmMatch(APPLICATION_ID);

    verify(notificationService).sendPairingConfirmation(mentor, mentee, cycle);
  }
}
