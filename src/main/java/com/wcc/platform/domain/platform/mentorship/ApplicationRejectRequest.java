package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for admin rejecting a mentee application.
 */
public record ApplicationRejectRequest(
    @NotBlank(message = "Reason is required when rejecting an application")
    @Size(max = 500, message = "Rejection reason cannot exceed 500 characters")
    String reason
) {
}
