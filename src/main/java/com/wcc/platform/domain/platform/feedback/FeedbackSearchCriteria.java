package com.wcc.platform.domain.platform.feedback;

import com.wcc.platform.domain.platform.type.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackSearchCriteria {
  private Long reviewerId;
  private Long revieweeId;
  private Long mentorshipCycleId;
  private FeedbackType feedbackType;
  private Integer year;
  private Boolean isAnonymous;
  private Boolean isApproved;
}
