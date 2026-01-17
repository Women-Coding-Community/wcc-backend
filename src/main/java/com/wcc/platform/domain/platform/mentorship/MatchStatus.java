package com.wcc.platform.domain.platform.mentorship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing the status of a confirmed mentorship match.
 * Corresponds to the match_status enum in the database.
 * Tracks the lifecycle of a mentor-mentee pairing from activation to completion.
 */
@Getter
@RequiredArgsConstructor
public enum MatchStatus {
    ACTIVE("active", "Currently active mentorship"),
    COMPLETED("completed", "Successfully completed"),
    CANCELLED("cancelled", "Cancelled by either party or admin"),
    ON_HOLD("on_hold", "Temporarily paused");

    private final String value;
    private final String description;

    /**
     * Get MatchStatus from database string value.
     *
     * @param value the database string value
     * @return the corresponding MatchStatus
     * @throws IllegalArgumentException if the value doesn't match any enum
     */
    public static MatchStatus fromValue(final String value) {
        for (final MatchStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown match status: " + value);
    }

    /**
     * Check if the match is in a terminal state (no longer active).
     *
     * @return true if status is terminal
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    /**
     * Check if the match is currently ongoing.
     *
     * @return true if active or on hold
     */
    public boolean isOngoing() {
        return this == ACTIVE || this == ON_HOLD;
    }

    @Override
    public String toString() {
        return value;
    }
}
