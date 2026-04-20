package com.wcc.platform.service;

import static com.wcc.platform.utils.MenteeApplicationTestBuilder.pending;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.MenteeApplicationResponse;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MenteeApplicationEnrichedTest {

  private static final Long MENTEE_ID = 10L;
  private static final Long MENTOR_ID = 20L;

  @Mock private MenteeApplicationRepository applicationRepository;
  @Mock private MentorRepository mentorRepository;
  @Mock private MentorshipMatchRepository matchRepository;
  @Mock private MentorshipCycleRepository cycleRepository;
  @Mock private MenteeRepository menteeRepository;

  private MenteeWorkflowService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service =
        new MenteeWorkflowService(
            applicationRepository,
            mentorRepository,
            matchRepository,
            cycleRepository,
            menteeRepository);
  }

  @Test
  @DisplayName(
      "Given mentor has applications, when getting applications, "
          + "then response contains enriched mentee information")
  void shouldReturnEnrichedApplicationsWithMenteeInfo() {
    final MenteeApplication application = pending(1L, MENTEE_ID, 1);
    final Mentee mentee =
        Mentee.menteeBuilder()
            .id(MENTEE_ID)
            .fullName("Jane Doe")
            .bio("A passionate developer")
            .network(
                List.of(
                    new SocialNetwork(
                        SocialNetworkType.LINKEDIN, "https://linkedin.com/in/janedoe")))
            .spokenLanguages(List.of("English"))
            .availableHsMonth(10)
            .build();

    when(applicationRepository.findByMentor(MENTOR_ID)).thenReturn(List.of(application));
    when(menteeRepository.findAllById(List.of(MENTEE_ID))).thenReturn(List.of(mentee));

    final List<MenteeApplicationResponse> result = service.getMentorApplications(MENTOR_ID);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).menteeName()).isEqualTo("Jane Doe");
    assertThat(result.get(0).menteeLinkedIn()).isEqualTo("https://linkedin.com/in/janedoe");
    assertThat(result.get(0).menteeBio()).isEqualTo("A passionate developer");
    assertThat(result.get(0).applicationId()).isEqualTo(1L);
    assertThat(result.get(0).menteeId()).isEqualTo(MENTEE_ID);
  }

  @Test
  @DisplayName(
      "Given mentor has no applications, when getting applications, then empty list is returned")
  void shouldReturnEmptyListWhenMentorHasNoApplications() {
    when(applicationRepository.findByMentor(MENTOR_ID)).thenReturn(List.of());

    final List<MenteeApplicationResponse> result = service.getMentorApplications(MENTOR_ID);

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName(
      "Given mentee not found in repository, when getting applications, "
          + "then response has null enriched fields")
  void shouldReturnNullEnrichedFieldsWhenMenteeNotFound() {
    final MenteeApplication application = pending(1L, MENTEE_ID, 1);

    when(applicationRepository.findByMentor(MENTOR_ID)).thenReturn(List.of(application));
    when(menteeRepository.findAllById(List.of(MENTEE_ID))).thenReturn(List.of());

    final List<MenteeApplicationResponse> result = service.getMentorApplications(MENTOR_ID);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).menteeName()).isNull();
    assertThat(result.get(0).menteeLinkedIn()).isNull();
    assertThat(result.get(0).menteeBio()).isNull();
  }

  @Test
  @DisplayName(
      "Given mentee has no LinkedIn, when getting applications, "
          + "then menteeLinkedIn is null but other fields are populated")
  void shouldReturnNullLinkedInWhenMenteeHasNoLinkedIn() {
    final MenteeApplication application = pending(1L, MENTEE_ID, 1);
    final Mentee mentee =
        Mentee.menteeBuilder()
            .id(MENTEE_ID)
            .fullName("Jane Doe")
            .bio("A passionate developer")
            .network(
                List.of(new SocialNetwork(SocialNetworkType.GITHUB, "https://github.com/janedoe")))
            .spokenLanguages(List.of("English"))
            .availableHsMonth(10)
            .build();

    when(applicationRepository.findByMentor(MENTOR_ID)).thenReturn(List.of(application));
    when(menteeRepository.findAllById(List.of(MENTEE_ID))).thenReturn(List.of(mentee));

    final List<MenteeApplicationResponse> result = service.getMentorApplications(MENTOR_ID);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).menteeName()).isEqualTo("Jane Doe");
    assertThat(result.get(0).menteeLinkedIn()).isNull();
    assertThat(result.get(0).menteeBio()).isEqualTo("A passionate developer");
  }
}
