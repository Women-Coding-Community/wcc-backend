package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Request DTO for submitting mentee applications to mentors.
 */
public record ApplicationSubmitRequest(
    @NotNull(message = "Cycle ID is required")
    Long cycleId,

    @NotEmpty(message = "Must apply to at least one mentor")
    @Size(max = 5, message = "Cannot apply to more than 5 mentors")
    List<@NotNull @Min(1) Long> mentorIds,

    @Size(max = 1000, message = "Application message cannot exceed 1000 characters")
    String message
) {
}
