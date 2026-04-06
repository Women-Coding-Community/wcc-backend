package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupFactories.createMemberTest;
import static com.wcc.platform.factories.SetupFeedbackFactories.createCommunityGeneralFeedbackDtoTest;
import static com.wcc.platform.factories.SetupFeedbackFactories.createCommunityGeneralFeedbackTest;
import static com.wcc.platform.factories.SetupFeedbackFactories.createMentorReviewFeedbackDtoTest;
import static com.wcc.platform.factories.SetupFeedbackFactories.createMentorReviewFeedbackTest;
import static com.wcc.platform.factories.SetupFeedbackFactories.createMentorshipProgramFeedbackDtoTest;
import static com.wcc.platform.factories.SetupFeedbackFactories.createMentorshipProgramFeedbackTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.exceptions.FeedbackNotFoundException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.feedback.Feedback;
import com.wcc.platform.domain.platform.feedback.FeedbackDto;
import com.wcc.platform.domain.platform.feedback.FeedbackSearchCriteria;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.repository.FeedbackRepository;
import com.wcc.platform.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("Feedback Service Tests")
class FeedbackServiceTest {
  @Mock private FeedbackRepository feedbackRepository;
  @Mock private MemberRepository memberRepository;
  private FeedbackService service;
  private Feedback feedback;
  private Feedback communityFeedback;
  private Feedback mentorshipFeedback;
  private FeedbackDto feedbackDto;
  private FeedbackDto communityFeedbackDto;
  private FeedbackDto mentorshipFeedbackDto;
  private Member reviewer;
  private Member reviewee;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new FeedbackService(feedbackRepository, memberRepository);

    feedback = createMentorReviewFeedbackTest();
    communityFeedback = createCommunityGeneralFeedbackTest();
    mentorshipFeedback = createMentorshipProgramFeedbackTest();
    feedbackDto = createMentorReviewFeedbackDtoTest();
    communityFeedbackDto = createCommunityGeneralFeedbackDtoTest();
    mentorshipFeedbackDto = createMentorshipProgramFeedbackDtoTest();
    reviewer = createMemberTest(com.wcc.platform.domain.platform.type.MemberType.MENTOR);
    reviewer.setId(feedbackDto.getReviewerId());
    reviewee = createMemberTest(com.wcc.platform.domain.platform.type.MemberType.MENTEE);
    reviewee.setId(feedbackDto.getRevieweeId());
  }

  @Test
  @DisplayName("Should create feedback successfully")
  void testCreateFeedback() {
    when(memberRepository.findById(feedbackDto.getReviewerId())).thenReturn(Optional.of(reviewer));
    when(memberRepository.findById(feedbackDto.getRevieweeId())).thenReturn(Optional.of(reviewee));
    when(feedbackRepository.create(any(Feedback.class))).thenReturn(feedback);

    final Feedback result = service.createFeedback(feedbackDto);

    assertEquals(feedback, result);
    assertThat(result.getReviewerId()).isEqualTo(feedbackDto.getReviewerId());
    verify(memberRepository).findById(feedbackDto.getReviewerId());
    verify(memberRepository).findById(feedbackDto.getRevieweeId());
    verify(feedbackRepository).create(any(Feedback.class));
  }

  @Test
  @DisplayName("Should create community general feedback without reviewee")
  void testCreateCommunityGeneralFeedbackNoReviewee() {
    when(memberRepository.findById(communityFeedbackDto.getReviewerId()))
        .thenReturn(Optional.of(reviewer));
    when(feedbackRepository.create(any(Feedback.class))).thenReturn(communityFeedback);

    final Feedback result = service.createFeedback(communityFeedbackDto);

    assertEquals(communityFeedback, result);
    assertThat(result.getRevieweeId()).isNull();
    verify(memberRepository).findById(anyLong());
    verify(feedbackRepository).create(any(Feedback.class));
  }

  @Test
  @DisplayName("Should create mentorship program feedback")
  void testCreateMentorshipProgramFeedbackSuccess() {
    when(memberRepository.findById(mentorshipFeedbackDto.getReviewerId()))
        .thenReturn(Optional.of(reviewer));
    when(feedbackRepository.create(any(Feedback.class))).thenReturn(mentorshipFeedback);

    final Feedback result = service.createFeedback(mentorshipFeedbackDto);

    assertEquals(mentorshipFeedback, result);
    assertThat(result.getMentorshipCycleId()).isNotNull();
    assertThat(result.getRevieweeId()).isNull();
    verify(memberRepository).findById(anyLong());
    verify(feedbackRepository).create(any(Feedback.class));
  }

  @Test
  @DisplayName("Should throw MemberNotFoundException when reviewer not found")
  void testCreateFeedbackReviewerNotFound() {
    when(memberRepository.findById(feedbackDto.getReviewerId())).thenReturn(Optional.empty());

    assertThrows(MemberNotFoundException.class, () -> service.createFeedback(feedbackDto));
    verify(feedbackRepository, never()).create(any(Feedback.class));
  }

  @Test
  @DisplayName("Should throw MemberNotFoundException when reviewee not found")
  void testCreateFeedbackRevieweeNotFound() {
    when(memberRepository.findById(feedbackDto.getReviewerId())).thenReturn(Optional.of(reviewer));
    when(memberRepository.findById(feedbackDto.getRevieweeId())).thenReturn(Optional.empty());

    assertThrows(MemberNotFoundException.class, () -> service.createFeedback(feedbackDto));
    verify(feedbackRepository, never()).create(any(Feedback.class));
  }

  @Test
  @DisplayName("Should update feedback successfully")
  void testUpdateFeedback() {
    final Long feedbackId = 1L;
    when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));
    when(memberRepository.findById(feedbackDto.getReviewerId())).thenReturn(Optional.of(reviewer));
    when(memberRepository.findById(feedbackDto.getRevieweeId())).thenReturn(Optional.of(reviewee));
    when(feedbackRepository.update(eq(feedbackId), any(Feedback.class))).thenReturn(feedback);

    Feedback result = service.updateFeedback(feedbackId, feedbackDto);

    assertThat(result.getId()).isEqualTo(feedbackId);
    verify(feedbackRepository).findById(feedbackId);
    verify(memberRepository).findById(feedbackDto.getReviewerId());
    verify(memberRepository).findById(feedbackDto.getRevieweeId());
    verify(feedbackRepository).update(eq(feedbackId), any(Feedback.class));
  }

  @Test
  @DisplayName("Should throw FeedbackNotFoundException when updating non-existent feedback")
  void testUpdateFeedbackNotFound() {
    final Long feedbackId = 999L;
    when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

    assertThrows(
        FeedbackNotFoundException.class, () -> service.updateFeedback(feedbackId, feedbackDto));
    verify(feedbackRepository, never()).update(anyLong(), any(Feedback.class));
  }

  @Test
  @DisplayName("Should get feedback by ID successfully")
  void testGetFeedbackById() {
    final Long feedbackId = 1L;
    when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));

    final Feedback result = service.getFeedbackById(feedbackId);
    assertThat(result.getId()).isEqualTo(feedbackId);
    assertEquals(feedback, result);
    verify(feedbackRepository).findById(feedbackId);
  }

  @Test
  @DisplayName("Should throw FeedbackNotFoundException when getting non-existent feedback")
  void testGetFeedbackByIdNotFound() {
    final Long feedbackId = 999L;
    when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

    assertThrows(FeedbackNotFoundException.class, () -> service.getFeedbackById(feedbackId));
  }

  @Test
  @DisplayName("Should approve feedback successfully")
  void testApproveFeedback() {
    final Long feedbackId = 1L;
    when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));

    service.approveFeedback(feedbackId);

    verify(feedbackRepository).approveFeedback(feedbackId);
  }

  @Test
  @DisplayName("Should throw FeedbackNotFoundException when approving non-existent feedback")
  void testApproveFeedbackNotFound() {
    final Long feedbackId = 999L;
    when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

    assertThrows(FeedbackNotFoundException.class, () -> service.approveFeedback(feedbackId));
    verify(feedbackRepository, never()).approveFeedback(anyLong());
  }

  @Test
  @DisplayName("Should set feedback anonymous status successfully")
  void testUpdateFeedbackAnonymousStatus() {
    final Long feedbackId = 1L;
    when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));

    service.updateFeedbackAnonymousStatus(feedbackId, true);

    verify(feedbackRepository, times(1)).updateAnonymousStatus(feedbackId, true);
  }

  @Test
  @DisplayName("Should delete feedback successfully")
  void testDeleteFeedback() {
    final Long feedbackId = 1L;
    when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.of(feedback));

    service.deleteFeedback(feedbackId);

    verify(feedbackRepository).deleteById(feedbackId);
  }

  @Test
  @DisplayName("Should throw FeedbackNotFoundException when deleting non-existent feedback")
  void testDeleteFeedbackNotFound() {
    final Long feedbackId = 999L;
    when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

    assertThrows(FeedbackNotFoundException.class, () -> service.deleteFeedback(feedbackId));
    verify(feedbackRepository, never()).deleteById(anyLong());
  }

  @Test
  @DisplayName("Should get all feedback with null criteria")
  void testGetAllFeedbackNullCriteria() {
    Feedback feedback1 = createMentorReviewFeedbackTest();
    Feedback feedback2 = createCommunityGeneralFeedbackTest();
    List<Feedback> expectedList = List.of(feedback1, feedback2);

    when(feedbackRepository.getAll(null)).thenReturn(expectedList);

    List<Feedback> result = service.getAllFeedback(null);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertEquals(expectedList, result);
    verify(feedbackRepository).getAll(null);
  }

  @Test
  @DisplayName("Should get all feedback with empty criteria")
  void testGetAllFeedbackEmptyCriteria() {
    FeedbackSearchCriteria criteria = FeedbackSearchCriteria.builder().build();
    Feedback feedback1 = createMentorReviewFeedbackTest();
    Feedback feedback2 = createMentorshipProgramFeedbackTest();
    List<Feedback> expectedList = List.of(feedback1, feedback2);

    when(feedbackRepository.getAll(criteria)).thenReturn(expectedList);

    List<Feedback> result = service.getAllFeedback(criteria);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertEquals(expectedList, result);
    verify(feedbackRepository).getAll(criteria);
  }

  @Test
  @DisplayName("Should get all feedback with multiple criteria")
  void testGetAllFeedbackMultipleCriteria() {
    Long reviewerId = 1L;
    Long revieweeId = 2L;
    Integer year = 2026;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder()
            .reviewerId(reviewerId)
            .revieweeId(revieweeId)
            .year(year)
            .build();

    Feedback feedback1 = createMentorReviewFeedbackTest();
    Feedback feedback2 = createMentorReviewFeedbackTest();
    feedback2.setId(2L);
    List<Feedback> expectedList = List.of(feedback1, feedback2);

    when(memberRepository.findById(reviewerId)).thenReturn(Optional.of(reviewer));
    when(memberRepository.findById(revieweeId)).thenReturn(Optional.of(reviewee));
    when(feedbackRepository.getAll(criteria)).thenReturn(expectedList);

    List<Feedback> result = service.getAllFeedback(criteria);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertEquals(expectedList, result);
    verify(memberRepository).findById(reviewerId);
    verify(memberRepository).findById(revieweeId);
    verify(feedbackRepository).getAll(criteria);
  }

  @Test
  @DisplayName("Should throw MemberNotFoundException when reviewer not found in getAll")
  void testGetAllFeedbackReviewerNotFound() {
    Long reviewerId = 999L;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().reviewerId(reviewerId).build();

    when(memberRepository.findById(reviewerId)).thenReturn(Optional.empty());

    assertThrows(MemberNotFoundException.class, () -> service.getAllFeedback(criteria));
    verify(memberRepository).findById(reviewerId);
    verify(feedbackRepository, never()).getAll(any());
  }

  @Test
  @DisplayName("Should throw MemberNotFoundException when reviewee not found in getAll")
  void testGetAllFeedbackRevieweeNotFound() {
    Long reviewerId = 1L;
    Long revieweeId = 999L;
    FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder().reviewerId(reviewerId).revieweeId(revieweeId).build();

    when(memberRepository.findById(reviewerId)).thenReturn(Optional.of(reviewer));
    when(memberRepository.findById(revieweeId)).thenReturn(Optional.empty());

    assertThrows(MemberNotFoundException.class, () -> service.getAllFeedback(criteria));
    verify(memberRepository).findById(reviewerId);
    verify(memberRepository).findById(revieweeId);
    verify(feedbackRepository, never()).getAll(any());
  }
}
