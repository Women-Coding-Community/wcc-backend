package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * Domain entity representing a confirmed mentor-mentee pairing. Corresponds to the
 * mentorship_matches table in the database. Created when the mentorship team confirms a match from
 * an accepted application.
 */
@SuppressWarnings("PMD.TooManyFields")
@Data
@Builder
public class MentorshipMatch {

  @NotNull private Long mentorId;
  private Long matchId;

  @NotNull private Long menteeId;

  @NotNull private Long cycleId;

  private Long applicationId;

  @NotNull private MatchStatus status;

  @NotNull private LocalDate startDate;

  private LocalDate endDate;
  private LocalDate expectedEndDate;
  private String sessionFrequency;
  private Integer totalSessions;
  private String cancellationReason;
  private String cancelledBy;
  private ZonedDateTime cancelledAt;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;

  /**
   * Check if the match is currently active.
   *
   * @return true if status is ACTIVE
   */
  public boolean isActive() {
    return status == MatchStatus.ACTIVE;
  }

  /**
   * Check if the match has been completed.
   *
   * @return true if status is COMPLETED
   */
  public boolean isCompleted() {
    return status == MatchStatus.COMPLETED;
  }

  /**
   * Check if the match was cancelled.
   *
   * @return true if status is CANCELLED
   */
  public boolean isCancelled() {
    return status == MatchStatus.CANCELLED;
  }

  /**
   * Get the duration of the mentorship in days.
   *
   * @return number of days from start to end (or current date if ongoing)
   */
  public long getDurationInDays() {
    final LocalDate end = endDate != null ? endDate : LocalDate.now();
    return java.time.temporal.ChronoUnit.DAYS.between(startDate, end);
  }

  /**
   * Check if the match has exceeded its expected end date.
   *
   * @return true if past expected end date
   */
  public boolean isPastExpectedEndDate() {
    return expectedEndDate != null
        && LocalDate.now().isAfter(expectedEndDate)
        && status.isOngoing();
  }

  /**
   * Get the number of days remaining until expected end date.
   *
   * @return days remaining, or 0 if no expected end date or already past
   */
  public long getDaysRemaining() {
    if (expectedEndDate == null || !status.isOngoing()) {
      return 0;
    }

    final long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expectedEndDate);

    return Math.max(0, days);
  }

  /** Increment the session count. */
  public void incrementSessionCount() {
    if (totalSessions == null) {
      totalSessions = 1;
    } else {
      totalSessions++;
    }
  }

  /**
   * Cancel the match with reason and actor.
   *
   * @param reason why the match was cancelled
   * @param cancelledBy who cancelled (mentor/mentee/admin)
   */
  public void cancel(final String reason, final String cancelledBy) {
    this.status = MatchStatus.CANCELLED;
    this.cancellationReason = reason;
    this.cancelledBy = cancelledBy;
    this.cancelledAt = ZonedDateTime.now();
    this.endDate = LocalDate.now();
  }

  /** Complete the match successfully. */
  public void complete() {
    this.status = MatchStatus.COMPLETED;
    this.endDate = LocalDate.now();
  }
}
