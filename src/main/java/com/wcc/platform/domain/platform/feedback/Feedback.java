package com.wcc.platform.domain.platform.feedback;

import com.wcc.platform.domain.platform.type.FeedbackType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Feedback for tracking member feedback. */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

  private Long id;
  @NotNull private Long reviewerId;
  private String reviewerName;
  private Long revieweeId; // For MENTOR_REVIEW
  private String revieweeName;
  private Long mentorshipCycleId; // For MENTORSHIP_PROGRAM
  @NotNull private FeedbackType feedbackType;

  @Min(1)
  @Max(5)
  private Integer rating;

  @NotBlank private String feedbackText;
  private Integer year;
  private Boolean isAnonymous;
  private Boolean isApproved;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}
