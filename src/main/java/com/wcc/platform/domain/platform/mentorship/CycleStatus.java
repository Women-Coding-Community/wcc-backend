package com.wcc.platform.domain.platform.mentorship;

import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing the status of a mentorship cycle.
 * Corresponds to the cycle_statuses table in the database.
 */
@Getter
@AllArgsConstructor
public enum CycleStatus {
    DRAFT(1, "Cycle created but not yet open for registration"),
    OPEN(2, "Registration is currently open"),
    CLOSED(3, "Registration has closed"),
    IN_PROGRESS(4, "Cycle is active, mentorship ongoing"),
    COMPLETED(5, "Cycle has finished successfully"),
    CANCELLED(6, "Cycle was cancelled");

    private final int statusId;
    private final String description;

    /**
     * Get CycleStatus from database integer ID.
     *
     * @param statusId the database integer ID
     * @return the corresponding CycleStatus
     * @throws IllegalArgumentException if the ID doesn't match any enum
     */
    public static CycleStatus fromId(final int statusId) {
        for (final CycleStatus status : values()) {
            if (status.statusId == statusId) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown cycle status ID: " + statusId);
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT).replace('_', ' ');
    }
}
