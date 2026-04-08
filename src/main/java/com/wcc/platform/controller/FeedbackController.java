package com.wcc.platform.controller;

import com.wcc.platform.domain.platform.feedback.Feedback;
import com.wcc.platform.domain.platform.feedback.FeedbackDto;
import com.wcc.platform.domain.platform.feedback.FeedbackSearchCriteria;
import com.wcc.platform.domain.platform.type.FeedbackType;
import com.wcc.platform.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for feedback APIs. */
@RestController
@RequestMapping("/api/platform/v1/feedback")
@SecurityRequirement(name = "apiKey")
@Tag(name = "Feedback", description = "Feedback management APIs")
@AllArgsConstructor
public class FeedbackController {

  private final FeedbackService feedbackService;

  /**
   * API to retrieve all feedback with optional filters.
   *
   * @param reviewerId Filter by reviewer ID
   * @param revieweeId Filter by reviewee I)
   * @param mentorshipCycleId Filter by mentorship cycle ID
   * @param feedbackType Filter by feedback type
   * @param year Filter by year
   * @param isAnonymous Filter by anonymous status
   * @param isApproved Filter by approval status
   * @return List of feedback matching the criteria
   */
  @GetMapping
  @Operation(summary = "Get all feedback with optional filters")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<Feedback>> getAllFeedback(
      @Parameter(description = "Reviewer ID") @RequestParam(required = false) final Long reviewerId,
      @Parameter(description = "Reviewee ID") @RequestParam(required = false) final Long revieweeId,
      @Parameter(description = "Mentorship Cycle ID") @RequestParam(required = false)
          final Long mentorshipCycleId,
      @Parameter(description = "Feedback Type") @RequestParam(required = false)
          final FeedbackType feedbackType,
      @Parameter(description = "Year") @RequestParam(required = false) final Integer year,
      @Parameter(description = "Anonymous status") @RequestParam(required = false)
          final Boolean isAnonymous,
      @Parameter(description = "Approved status") @RequestParam(required = false)
          final Boolean isApproved) {
    final FeedbackSearchCriteria criteria =
        FeedbackSearchCriteria.builder()
            .reviewerId(reviewerId)
            .revieweeId(revieweeId)
            .mentorshipCycleId(mentorshipCycleId)
            .feedbackType(feedbackType)
            .year(year)
            .isAnonymous(isAnonymous)
            .isApproved(isApproved)
            .build();

    final List<Feedback> feedback = feedbackService.getAllFeedback(criteria);
    return ResponseEntity.ok(feedback);
  }

  /**
   * API to retrieve feedback by ID.
   *
   * @param feedbackId ID of the feedback
   * @return Feedback details
   */
  @GetMapping("/{feedbackId}")
  @Operation(summary = "Get feedback by ID")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Feedback> getFeedbackById(
      @Parameter(description = "ID of the feedback") @PathVariable final Long feedbackId) {
    final Feedback feedback = feedbackService.getFeedbackById(feedbackId);
    return ResponseEntity.ok(feedback);
  }

  /**
   * API to create feedback.
   *
   * @param feedbackDto DTO containing feedback data
   * @return Created feedback
   */
  @PostMapping
  @Operation(summary = "Create feedback")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Feedback> createFeedback(
      @Valid @RequestBody final FeedbackDto feedbackDto) {
    final Feedback feedback = feedbackService.createFeedback(feedbackDto);
    return new ResponseEntity<>(feedback, HttpStatus.CREATED);
  }

  /**
   * API to update feedback.
   *
   * @param feedbackId ID of the feedback to update
   * @param feedbackDto DTO with updated feedback data
   * @return Updated feedback
   */
  @PutMapping("/{feedbackId}")
  @Operation(summary = "Update feedback")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Feedback> updateFeedback(
      @Parameter(description = "ID of the feedback") @PathVariable final Long feedbackId,
      @Valid @RequestBody final FeedbackDto feedbackDto) {
    final Feedback feedback = feedbackService.updateFeedback(feedbackId, feedbackDto);
    return ResponseEntity.ok(feedback);
  }

  /**
   * API to approve feedback (admin only).
   *
   * @param feedbackId ID of the feedback to approve
   * @return No content
   */
  @PatchMapping("/{feedbackId}/approve")
  @Operation(summary = "Approve feedback (admin only)")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Void> approveFeedback(
      @Parameter(description = "ID of the feedback") @PathVariable final Long feedbackId) {
    feedbackService.approveFeedback(feedbackId);
    return ResponseEntity.ok().build();
  }

  /**
   * API to set feedback anonymous status (to hide/show reviewer name).
   *
   * @param feedbackId ID of the feedback
   * @param isAnonymous true to hide reviewer name, false to show reviewer name
   * @return No content
   */
  @PatchMapping("/{feedbackId}/anonymous-status")
  @Operation(summary = "Update feedback anonymous status (to hide/show reviewer name)")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Void> updateFeedbackAnonymousStatus(
      @Parameter(description = "ID of the feedback") @PathVariable final Long feedbackId,
      @Parameter(description = "Is anonymous") @RequestParam final Boolean isAnonymous) {
    feedbackService.updateFeedbackAnonymousStatus(feedbackId, isAnonymous);
    return ResponseEntity.ok().build();
  }

  /**
   * API to delete feedback.
   *
   * @param feedbackId ID of the feedback to delete
   * @return No content
   */
  @DeleteMapping("/{feedbackId}")
  @Operation(summary = "Delete feedback")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> deleteFeedback(
      @Parameter(description = "ID of the feedback") @PathVariable final Long feedbackId) {
    feedbackService.deleteFeedback(feedbackId);
    return ResponseEntity.noContent().build();
  }
}
