package com.wcc.platform.domain.platform.mentorship;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing the status of a mentorship cycle.
 * Corresponds to the cycle_status enum in the database.
 */
@Getter
@RequiredArgsConstructor
public enum CycleStatus {
    DRAFT("draft", "Cycle created but not yet open for registration"),
    OPEN("open", "Registration is currently open"),
    CLOSED("closed", "Registration has closed"),
    IN_PROGRESS("in_progress", "Cycle is active, mentorship ongoing"),
    COMPLETED("completed", "Cycle has finished successfully"),
    CANCELLED("cancelled", "Cycle was cancelled");

    private final String value;
    private final String description;

    /**
     * Get CycleStatus from database string value.
     *
     * @param value the database string value
     * @return the corresponding CycleStatus
     * @throws IllegalArgumentException if the value doesn't match any enum
     */
    public static CycleStatus fromValue(final String value) {
        for (CycleStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown cycle status: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
