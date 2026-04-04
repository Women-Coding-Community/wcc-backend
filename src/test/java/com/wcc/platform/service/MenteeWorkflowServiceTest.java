package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.exceptions.ApplicationNotFoundException;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.MenteeApplicationReviewDto;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MenteeRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MenteeWorkflowServiceTest {

  private static final String REJECTION_REASON =
      "Application does not meet the eligibility criteria for this mentorship cycle";

  @Mock private MenteeApplicationRepository applicationRepository;
  @Mock private MenteeRepository menteeRepository;
  @Mock private MentorshipMatchRepository matchRepository;
  @Mock private MentorshipCycleRepository cycleRepository;

  private MenteeWorkflowService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service =
        new MenteeWorkflowService(
            applicationRepository, menteeRepository, matchRepository, cycleRepository);
  }

  @Test
  @DisplayName(
      "Given a PENDING application, when admin approves, then status becomes MENTOR_REVIEWING")
  void shouldApprovePendingApplicationAndUpdateStatusToMentorReviewing() {
    final MenteeApplication pending = pendingApplication(1L, 10L, 1);
    final MenteeApplication approved = reviewingApplication(1L, 10L);

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pending));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.MENTOR_REVIEWING, null))
        .thenReturn(approved);

    final MenteeApplication result = service.approveApplication(1L);

    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.MENTOR_REVIEWING);
  }

  @Test
  @DisplayName(
      "Given non-PENDING application, when admin approves, then ContentNotFoundException is thrown")
  void shouldThrowContentNotFoundExceptionWhenApprovedApplicationIsNotPending() {
    final MenteeApplication reviewing =
        MenteeApplication.builder()
            .applicationId(2L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.MENTOR_REVIEWING)
            .whyMentor("Great mentor")
            .build();

    when(applicationRepository.findById(2L)).thenReturn(Optional.of(reviewing));

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
  @DisplayName(
      "Given a PENDING application, when admin rejects, then status becomes REJECTED")
  void shouldRejectPendingApplicationAndUpdateStatusToRejected() {
    final MenteeApplication pending = pendingApplication(1L, 10L, 1);
    final MenteeApplication rejected = rejectedApplication(1L, 10L);

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pending));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.REJECTED, REJECTION_REASON))
        .thenReturn(rejected);

    final MenteeApplication result = service.rejectApplication(1L, REJECTION_REASON);

    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
  }

  @Test
  @DisplayName(
      "Given non-PENDING application, when admin rejects, then ContentNotFoundException is thrown")
  void shouldThrowContentNotFoundExceptionWhenRejectedApplicationIsNotPending() {
    final MenteeApplication rejected =
        MenteeApplication.builder()
            .applicationId(2L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.REJECTED)
            .whyMentor("Great mentor")
            .build();

    when(applicationRepository.findById(2L)).thenReturn(Optional.of(rejected));

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
      "Given PENDING priority-1 apps exist, when getting reviews, then enriched DTOs are returned")
  void shouldReturnEnrichedDtosForPendingPriorityOneApplications() {
    final MenteeApplication app = pendingApplication(1L, 10L, 1);
    final Mentee mentee = menteeWithLinkedIn();

    when(applicationRepository.findByStatusAndPriorityOrder(ApplicationStatus.PENDING, 1))
        .thenReturn(List.of(app));
    when(menteeRepository.findById(10L)).thenReturn(Optional.of(mentee));

    final List<MenteeApplicationReviewDto> result = service.getPendingPriorityOneReviews();

    assertThat(result).hasSize(1);
    final MenteeApplicationReviewDto dto = result.getFirst();
    assertThat(dto.applicationId()).isEqualTo(1L);
    assertThat(dto.menteeId()).isEqualTo(10L);
    assertThat(dto.fullName()).isEqualTo("Jane Doe");
    assertThat(dto.email()).isEqualTo("jane@wcc.com");
    assertThat(dto.mentorshipGoal()).isEqualTo("Great mentor");
    assertThat(dto.yearsExperience()).isEqualTo(3);
    assertThat(dto.linkedinUrl()).isEqualTo("https://linkedin.com/jane");
  }

  @Test
  @DisplayName(
      "Given no PENDING priority-1 applications, when getting reviews, then empty list is returned")
  void shouldReturnEmptyListWhenNoPendingPriorityOneApplications() {
    when(applicationRepository.findByStatusAndPriorityOrder(ApplicationStatus.PENDING, 1))
        .thenReturn(List.of());

    final List<MenteeApplicationReviewDto> result = service.getPendingPriorityOneReviews();

    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName(
      "Given mentee not found for pending app, when getting reviews, then ContentNotFoundException")
  void shouldThrowContentNotFoundExceptionWhenMenteeNotFoundForPendingApplication() {
    final MenteeApplication app = pendingApplication(1L, 10L, 1);

    when(applicationRepository.findByStatusAndPriorityOrder(ApplicationStatus.PENDING, 1))
        .thenReturn(List.of(app));
    when(menteeRepository.findById(10L)).thenReturn(Optional.empty());

    assertThatThrownBy(service::getPendingPriorityOneReviews)
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("Mentee not found for application 1");
  }

  @Test
  @DisplayName(
      "Given mentee has a PENDING priority-1 app, when approving by menteeId, then it is approved")
  void shouldApprovePriorityOneApplicationWhenApprovingByMenteeId() {
    final MenteeApplication priorityOne = pendingApplication(1L, 10L, 1);
    final MenteeApplication priorityTwo = pendingApplication(2L, 10L, 2);
    final MenteeApplication approved = reviewingApplication(1L, 10L);

    when(applicationRepository.findPendingByMenteeId(10L))
        .thenReturn(List.of(priorityOne, priorityTwo));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.MENTOR_REVIEWING, null))
        .thenReturn(approved);

    final MenteeApplication result = service.approveMenteeByMenteeId(10L);

    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.MENTOR_REVIEWING);
    assertThat(result.getApplicationId()).isEqualTo(1L);
  }

  @Test
  @DisplayName(
      "Given mentee has no PENDING applications, when approving by menteeId, "
          + "then ContentNotFoundException is thrown")
  void shouldThrowContentNotFoundExceptionWhenNoAppsFoundForMenteeApproval() {
    when(applicationRepository.findPendingByMenteeId(10L)).thenReturn(List.of());

    assertThatThrownBy(() -> service.approveMenteeByMenteeId(10L))
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("No pending priority-1 application found for mentee 10");
  }

  @Test
  @DisplayName(
      "Given mentee has PENDING apps but none is priority-1, when approving by menteeId, "
          + "then ContentNotFoundException is thrown")
  void shouldThrowContentNotFoundExceptionWhenNoPriorityOneAppForMentee() {
    final MenteeApplication priorityTwo = pendingApplication(2L, 10L, 2);

    when(applicationRepository.findPendingByMenteeId(10L)).thenReturn(List.of(priorityTwo));

    assertThatThrownBy(() -> service.approveMenteeByMenteeId(10L))
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("No pending priority-1 application found for mentee 10");
  }

  @Test
  @DisplayName(
      "Given mentee has multiple PENDING applications, when rejecting by menteeId, "
          + "then all are rejected")
  void shouldRejectAllPendingApplicationsWhenRejectingByMenteeId() {
    final MenteeApplication app1 = pendingApplication(1L, 10L, 1);
    final MenteeApplication app2 = pendingApplication(2L, 10L, 2);
    final MenteeApplication rejected1 = rejectedApplication(1L, 10L);
    final MenteeApplication rejected2 = rejectedApplication(2L, 10L);

    when(applicationRepository.findPendingByMenteeId(10L)).thenReturn(List.of(app1, app2));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.REJECTED, REJECTION_REASON))
        .thenReturn(rejected1);
    when(applicationRepository.updateStatus(2L, ApplicationStatus.REJECTED, REJECTION_REASON))
        .thenReturn(rejected2);

    final List<MenteeApplication> result = service.rejectMenteeByMenteeId(10L, REJECTION_REASON);

    assertThat(result).hasSize(2);
    assertThat(result).allMatch(app -> app.getStatus() == ApplicationStatus.REJECTED);
  }

  @Test
  @DisplayName(
      "Given mentee has no PENDING applications, when rejecting by menteeId, "
          + "then ContentNotFoundException is thrown")
  void shouldThrowContentNotFoundExceptionWhenNoAppsFoundForMenteeRejection() {
    when(applicationRepository.findPendingByMenteeId(10L)).thenReturn(List.of());

    assertThatThrownBy(() -> service.rejectMenteeByMenteeId(10L, REJECTION_REASON))
        .isInstanceOf(ContentNotFoundException.class)
        .hasMessageContaining("No pending applications found for mentee 10");
  }

  private MenteeApplication pendingApplication(
      final Long applicationId, final Long menteeId, final int priority) {
    return MenteeApplication.builder()
        .applicationId(applicationId)
        .menteeId(menteeId)
        .mentorId(20L)
        .cycleId(5L)
        .priorityOrder(priority)
        .status(ApplicationStatus.PENDING)
        .whyMentor("Great mentor")
        .build();
  }

  private MenteeApplication reviewingApplication(
      final Long applicationId, final Long menteeId) {
    return MenteeApplication.builder()
        .applicationId(applicationId)
        .menteeId(menteeId)
        .mentorId(20L)
        .cycleId(5L)
        .priorityOrder(1)
        .status(ApplicationStatus.MENTOR_REVIEWING)
        .whyMentor("Great mentor")
        .build();
  }

  private MenteeApplication rejectedApplication(
      final Long applicationId, final Long menteeId) {
    return MenteeApplication.builder()
        .applicationId(applicationId)
        .menteeId(menteeId)
        .mentorId(20L)
        .cycleId(5L)
        .priorityOrder(1)
        .status(ApplicationStatus.REJECTED)
        .whyMentor("Great mentor")
        .build();
  }

  private Mentee menteeWithLinkedIn() {
    return Mentee.menteeBuilder()
        .id(10L)
        .fullName("Jane Doe")
        .email("jane@wcc.com")
        .position("Software Engineer")
        .slackDisplayName("jane.doe")
        .country(new Country("GB", "United Kingdom"))
        .bio("A motivated mentee")
        .skills(new Skills(3, List.of(), List.of(), List.of()))
        .availableHsMonth(5)
        .spokenLanguages(List.of("English"))
        .network(
            List.of(new SocialNetwork(SocialNetworkType.LINKEDIN, "https://linkedin.com/jane")))
        .build();
  }
}