package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.ApplicationNotFoundException;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.repository.MenteeApplicationRepository;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.repository.MentorshipMatchRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class MenteeWorkflowServiceTest {

  @Mock private MenteeApplicationRepository applicationRepository;
  @Mock private MentorshipMatchRepository matchRepository;
  @Mock private MentorshipCycleRepository cycleRepository;

  private MenteeWorkflowService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new MenteeWorkflowService(applicationRepository, matchRepository, cycleRepository);
  }

  @Test
  @DisplayName(
      "Given a PENDING application, when admin approves it, then status is updated to MENTOR_REVIEWING")
  void shouldApprovePendingApplicationAndUpdateStatusToMentorReviewing() {
    final MenteeApplication pending =
        MenteeApplication.builder()
            .applicationId(1L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.PENDING)
            .whyMentor("Great mentor")
            .build();

    final MenteeApplication approved =
        MenteeApplication.builder()
            .applicationId(1L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.MENTOR_REVIEWING)
            .whyMentor("Great mentor")
            .build();

    when(applicationRepository.findById(1L)).thenReturn(Optional.of(pending));
    when(applicationRepository.updateStatus(1L, ApplicationStatus.MENTOR_REVIEWING, null))
        .thenReturn(approved);

    final MenteeApplication result = service.approveApplication(1L);

    assertThat(result.getStatus()).isEqualTo(ApplicationStatus.MENTOR_REVIEWING);
  }

  @Test
  @DisplayName(
      "Given a non-PENDING application, when admin approves it, then ContentNotFoundException is thrown")
  void shouldThrowContentNotFoundExceptionWhenApplicationIsNotPending() {
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
      "Given an application that does not exist, when admin approves it, then ApplicationNotFoundException is thrown")
  void shouldThrowApplicationNotFoundExceptionWhenApplicationDoesNotExist() {
    when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.approveApplication(99L))
        .isInstanceOf(ApplicationNotFoundException.class)
        .hasMessageContaining("Application not found with ID: 99");
  }
}
