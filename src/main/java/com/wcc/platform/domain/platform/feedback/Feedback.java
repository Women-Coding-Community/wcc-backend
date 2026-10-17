package com.wcc.platform.domain.platform.feedback;

import com.wcc.platform.domain.platform.type.FeedbackType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Feedback for tracking member feedback. */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Builder(toBuilder = true)
public class Feedback {

  @Setter private Long id;
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

  @Min(2000)
  @Max(2100)
  private Integer year;

  @Setter private Boolean isAnonymous;
  @Setter private Boolean isApproved;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}
