package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for mentor declining an application.
 */
public record ApplicationDeclineRequest(
    @NotBlank(message = "Reason is required when declining an application")
    @Size(max = 500, message = "Decline reason cannot exceed 500 characters")
    String reason
) {
}
