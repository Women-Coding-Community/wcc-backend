package com.wcc.platform.domain.platform.feedback;

import com.wcc.platform.domain.platform.feedback.validation.ValidFeedback;
import com.wcc.platform.domain.platform.type.FeedbackType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** DTO for feedback creation/update - uses IDs for member identification. */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ValidFeedback
public class FeedbackDto {
  private Long id;
  @NotNull private Long reviewerId;
  private Long revieweeId;
  private Long mentorshipCycleId;
  @NotNull private FeedbackType feedbackType;

  @Min(1)
  @Max(5)
  private Integer rating;

  @NotBlank private String feedbackText;
  private Integer year;
  @NotNull private Boolean isAnonymous;

  /**
   * Convert to domain object.
   *
   * @return Feedback domain entity
   */
  public Feedback merge() {
    return Feedback.builder()
        .id(id)
        .reviewerId(reviewerId)
        .revieweeId(revieweeId)
        .mentorshipCycleId(mentorshipCycleId)
        .feedbackType(feedbackType)
        .rating(rating)
        .feedbackText(feedbackText)
        .year(year)
        .isAnonymous(isAnonymous)
        .isApproved(false) // Default - requires admin approval
        .build();
  }
}
