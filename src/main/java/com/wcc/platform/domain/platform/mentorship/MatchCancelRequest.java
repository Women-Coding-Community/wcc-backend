package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for cancelling a mentorship match.
 */
public record MatchCancelRequest(
    @NotBlank(message = "Cancellation reason is required")
    @Size(max = 500, message = "Cancellation reason cannot exceed 500 characters")
    String reason,

    @NotBlank(message = "Cancelled by field is required")
    @Size(max = 50, message = "Cancelled by cannot exceed 50 characters")
    String cancelledBy
) {
}
