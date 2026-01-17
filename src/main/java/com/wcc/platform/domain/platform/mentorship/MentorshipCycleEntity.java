package com.wcc.platform.domain.platform.mentorship;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * Domain entity representing a mentorship cycle.
 * Corresponds to the mentorship_cycles table in the database.
 * Replaces hardcoded cycle logic with database-driven configuration.
 */
@Data
@Builder
public class MentorshipCycleEntity {
    private Long cycleId;
    private Integer cycleYear;
    private MentorshipType mentorshipType;
    private Integer cycleMonth;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private LocalDate cycleStartDate;
    private LocalDate cycleEndDate;
    private CycleStatus status;
    private Integer maxMenteesPerMentor;
    private String description;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    /**
     * Check if registration is currently open based on current date.
     *
     * @return true if registration is open
     */
    public boolean isRegistrationOpen() {
        if (status != CycleStatus.OPEN) {
            return false;
        }

        final LocalDate now = LocalDate.now();
        return !now.isBefore(registrationStartDate) && !now.isAfter(registrationEndDate);
    }

    /**
     * Check if the cycle is currently active.
     *
     * @return true if cycle is in progress
     */
    public boolean isActive() {
        return status == CycleStatus.IN_PROGRESS;
    }

    /**
     * Convert to MentorshipCycle value object for backward compatibility.
     *
     * @return MentorshipCycle value object
     */
    public MentorshipCycle toMentorshipCycle() {
        return new MentorshipCycle(mentorshipType,
            cycleMonth != null ? java.time.Month.of(cycleMonth) : null);
    }
}
