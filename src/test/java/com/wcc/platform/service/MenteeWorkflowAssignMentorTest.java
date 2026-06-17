package com.wcc.platform.service;

import static com.wcc.platform.utils.MenteeApplicationTestBuilder.baseBuilder;
import static com.wcc.platform.utils.MenteeApplicationTestBuilder.rejected;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.DuplicateApplicationException;
import com.wcc.platform.domain.exceptions.MentorCapacityExceededException;
import com.wcc.platform.domain.exceptions.MentorNotFoundException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenteeWorkflowAssignMentorTest {

  private static final String ASSIGNMENT_NOTES = "Manually assigned mentor";
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
      "Given valid inputs when assigning mentor, "
          + "then new application is created with MENTOR_REVIEWING status")
  void shouldAssignMentorSuccessfully() {
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
    when(applicationRepository.create(any(MenteeApplication.class))).thenReturn(createdApp);

    final MenteeApplication result =
        service.assignMentor(MENTEE_ID, CYCLE_ID, MENTOR_ID, ASSIGNMENT_NOTES);

    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.PENDING);

    final ArgumentCaptor<MenteeApplication> captor =
        ArgumentCaptor.forClass(MenteeApplication.class);
    verify(applicationRepository).create(captor.capture());
    final MenteeApplication created = captor.getValue();
    assertThat(created.getMenteeId()).isEqualTo(MENTEE_ID);
    assertThat(created.getMentorId()).isEqualTo(MENTOR_ID);
    assertThat(created.getCycleId()).isEqualTo(CYCLE_ID);
    assertThat(created.getStatus()).isEqualTo(ApplicationStatus.MENTOR_REVIEWING);
  }

  @Test
  @DisplayName(
      "Given valid inputs when assigning mentor, "
          + "then sendNewMenteesNotification and sendApplicationUpdate are called")
  void shouldSendEmailNotificationsOnAssignMentor() {
    final Mentor mentor = Mentor.mentorBuilder().build();
    final MenteeApplication createdApp =
        baseBuilder(100L, MENTEE_ID, MENTOR_ID, 0)
            .status(ApplicationStatus.MENTOR_REVIEWING)
            .build();
    final MentorshipCycleEntity cycle =
        MentorshipCycleEntity.builder().cycleId(CYCLE_ID).maxMenteesPerMentor(3).build();

    when(mentorRepository.findById(MENTOR_ID)).thenReturn(Optional.of(mentor));
    when(applicationRepository.findByMenteeMentorCycle(MENTEE_ID, MENTOR_ID, CYCLE_ID))
        .thenReturn(Optional.empty());
    when(cycleRepository.findById(CYCLE_ID)).thenReturn(Optional.of(cycle));
    when(matchRepository.countActiveMenteesByMentorAndCycle(MENTOR_ID, CYCLE_ID)).thenReturn(0);
    when(applicationRepository.create(any(MenteeApplication.class))).thenReturn(createdApp);

    service.assignMentor(MENTEE_ID, CYCLE_ID, MENTOR_ID, ASSIGNMENT_NOTES);

    verify(notificationService).sendNewMenteesNotification(mentor, cycle);
    verify(notificationService).sendApplicationUpdate(Optional.empty(), createdApp);
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
}
