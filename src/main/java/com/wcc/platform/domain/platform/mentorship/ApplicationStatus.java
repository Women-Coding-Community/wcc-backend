package com.wcc.platform.domain.platform.mentorship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing the status of a mentee application to a mentor.
 * Corresponds to the application_status enum in the database.
 * Tracks the complete workflow from application submission to matching.
 */
@Getter
@RequiredArgsConstructor
public enum ApplicationStatus {
    PENDING("pending", "Mentee submitted application, awaiting mentor response"),
    MENTOR_REVIEWING("mentor_reviewing", "Mentor is actively reviewing the application"),
    MENTOR_ACCEPTED("mentor_accepted", "Mentor accepted, awaiting team confirmation"),
    MENTOR_DECLINED("mentor_declined", "Mentor declined this application"),
    MATCHED("matched", "Successfully matched and confirmed"),
    DROPPED("dropped", "Mentee withdrew application"),
    REJECTED("rejected", "Rejected by Mentorship Team"),
    EXPIRED("expired", "Application expired (no response within timeframe)");

    private final String value;
    private final String description;

    /**
     * Get ApplicationStatus from database string value.
     *
     * @param value the database string value
     * @return the corresponding ApplicationStatus
     * @throws IllegalArgumentException if the value doesn't match any enum
     */
    public static ApplicationStatus fromValue(final String value) {
        for (ApplicationStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown application status: " + value);
    }

    /**
     * Check if the application is in a terminal state (no further changes expected).
     *
     * @return true if status is terminal
     */
    public boolean isTerminal() {
        return this == MATCHED || this == REJECTED || this == DROPPED || this == EXPIRED;
    }

    /**
     * Check if the application is pending mentor action.
     *
     * @return true if awaiting mentor response
     */
    public boolean isPendingMentorAction() {
        return this == PENDING || this == MENTOR_REVIEWING;
    }

    /**
     * Check if the application has been accepted by mentor.
     *
     * @return true if mentor accepted
     */
    public boolean isMentorAccepted() {
        return this == MENTOR_ACCEPTED || this == MATCHED;
    }

    @Override
    public String toString() {
        return value;
    }
}
