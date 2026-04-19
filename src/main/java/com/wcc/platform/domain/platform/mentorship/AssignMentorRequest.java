package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for manually assigning a mentor to a mentee from the manual match queue.
 */
public record AssignMentorRequest(
    @NotNull(message = "Mentor ID is required")
    Long mentorId,

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    String notes
) {
}