package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * Domain entity representing a mentee's application to a specific mentor. Corresponds to the
 * mentee_applications table in the database. Supports priority-based mentor selection where mentees
 * can apply to multiple mentors with ranking (1 = highest priority, 5 = lowest).
 */
@Data
@Builder
public class MenteeApplication {
  private Long applicationId;

  @NotNull private Long menteeId;

  @NotNull private Long mentorId;

  @NotNull private Long cycleId;

  @NotNull
  @Min(1)
  @Max(5)
  private Integer priorityOrder;

  @NotNull private ApplicationStatus status;

  private String applicationMessage;
  private ZonedDateTime appliedAt;
  private ZonedDateTime reviewedAt;
  private ZonedDateTime matchedAt;
  private String mentorResponse;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;

  /**
   * Check if this application has been reviewed by the mentor.
   *
   * @return true if mentor has reviewed
   */
  public boolean isReviewed() {
    return reviewedAt != null;
  }

  /**
   * Check if this application has been matched.
   *
   * @return true if successfully matched
   */
  public boolean isMatched() {
    return status == ApplicationStatus.MATCHED && matchedAt != null;
  }

  /**
   * Check if this application can still be modified.
   *
   * @return true if not in terminal state
   */
  public boolean canBeModified() {
    return !status.isTerminal();
  }

  /**
   * Get the number of days since application was submitted.
   *
   * @return days since applied
   */
  public long getDaysSinceApplied() {
    if (appliedAt == null) {
      return 0;
    }
    return java.time.temporal.ChronoUnit.DAYS.between(
        appliedAt.toLocalDate(), ZonedDateTime.now().toLocalDate());
  }

  /**
   * Check if application should be expired based on days threshold.
   *
   * @param expiryDays number of days before expiry
   * @return true if should expire
   */
  public boolean shouldExpire(final int expiryDays) {
    return status.isPendingMentorAction() && getDaysSinceApplied() > expiryDays;
  }
}
