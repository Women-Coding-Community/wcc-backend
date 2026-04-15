package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for confirming no match was found for a mentee.
 */
public record NoMatchRequest(
    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    String reason
) {
}
