package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.FeedbackNotFoundException;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.feedback.Feedback;
import com.wcc.platform.domain.platform.feedback.FeedbackDto;
import com.wcc.platform.domain.platform.feedback.FeedbackSearchCriteria;
import com.wcc.platform.repository.FeedbackRepository;
import com.wcc.platform.repository.MemberRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Feedback service. */
@Slf4j
@Service
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final MemberRepository memberRepository;

  public FeedbackService(
      final FeedbackRepository feedbackRepository, final MemberRepository memberRepository) {
    this.feedbackRepository = feedbackRepository;
    this.memberRepository = memberRepository;
  }

  /**
   * Create new feedback. Validates that reviewer/reviewee exists (if provided).
   *
   * @return created Feedback
   */
  public Feedback createFeedback(final FeedbackDto feedbackDto) {
    validateReviewerExists(feedbackDto.getReviewerId());
    final Feedback feedback = feedbackDto.merge();

    if (feedback.getRevieweeId() != null) {
      validateRevieweeExists(feedback.getRevieweeId());
    }

    log.info(
        "Creating feedback of type {} from reviewer {} with text: {}",
        feedback.getFeedbackType(),
        feedback.getReviewerId(),
        feedback.getFeedbackText());

    return feedbackRepository.create(feedback);
  }

  /**
   * Update feedback. Only reviewer or admin can update.
   *
   * @return updated Feedback
   */
  public Feedback updateFeedback(final Long feedbackId, final FeedbackDto feedbackDto) {
    final Feedback existing =
        feedbackRepository
            .findById(feedbackId)
            .orElseThrow(() -> new FeedbackNotFoundException(feedbackId));

    validateReviewerExists(feedbackDto.getReviewerId());

    final Feedback updatedFeedback = feedbackDto.merge();

    updatedFeedback.setId(feedbackId);
    updatedFeedback.setIsApproved(existing.getIsApproved());
    updatedFeedback.setIsAnonymous(existing.getIsAnonymous());

    if (updatedFeedback.getRevieweeId() != null) {
      validateRevieweeExists(updatedFeedback.getRevieweeId());
    }

    return feedbackRepository.update(feedbackId, updatedFeedback);
  }

  /**
   * Get feedback by ID.
   *
   * @return Feedback if found
   */
  public Feedback getFeedbackById(final Long feedbackId) {
    return feedbackRepository
        .findById(feedbackId)
        .orElseThrow(() -> new FeedbackNotFoundException(feedbackId));
  }

  /** Approve feedback (admin only). */
  public void approveFeedback(final Long feedbackId) {
    feedbackRepository
        .findById(feedbackId)
        .orElseThrow(() -> new FeedbackNotFoundException(feedbackId));

    feedbackRepository.approveFeedback(feedbackId);
  }

  /** Update anonymous status of feedback (to hide/show reviewer name). */
  public void updateFeedbackAnonymousStatus(final Long feedbackId, final Boolean isAnonymous) {
    feedbackRepository
        .findById(feedbackId)
        .orElseThrow(() -> new FeedbackNotFoundException(feedbackId));

    feedbackRepository.updateAnonymousStatus(feedbackId, isAnonymous);
  }

  /** Delete feedback by ID. */
  public void deleteFeedback(final Long feedbackId) {
    feedbackRepository
        .findById(feedbackId)
        .orElseThrow(() -> new FeedbackNotFoundException(feedbackId));

    feedbackRepository.deleteById(feedbackId);
  }

  /**
   * Get all feedbacks matching the specified search criteria.
   *
   * @param criteria the search criteria to filter feedbacks
   * @return List of feedbacks
   */
  public List<Feedback> getAllFeedback(final FeedbackSearchCriteria criteria) {
    if (criteria != null && criteria.getReviewerId() != null) {
      validateReviewerExists(criteria.getReviewerId());
    }
    if (criteria != null && criteria.getRevieweeId() != null) {
      validateRevieweeExists(criteria.getRevieweeId());
    }

    return feedbackRepository.getAll(criteria);
  }

  /**
   * Validate that reviewer with given ID exists.
   *
   * @return void, throws exception if reviewer not found
   */
  private void validateReviewerExists(final Long reviewerId) {
    memberRepository
        .findById(reviewerId)
        .orElseThrow(
            () -> {
              log.warn("Reviewer with ID {} not found", reviewerId);
              return new MemberNotFoundException(reviewerId);
            });
  }

  /**
   * Validate that reviewee with given ID exists.
   *
   * @return void, throws exception if reviewee not found
   */
  private void validateRevieweeExists(final Long revieweeId) {
    memberRepository
        .findById(revieweeId)
        .orElseThrow(
            () -> {
              log.warn("Reviewee with ID {} not found", revieweeId);
              return new MemberNotFoundException(revieweeId);
            });
  }
}
