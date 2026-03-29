package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request DTO for admin rejecting a mentee application. */
public record ApplicationRejectRequest(
    @NotBlank(message = "Reason is required when rejecting an application")
        @Size(
            min = 50,
            max = 500,
            message = "Rejection reason must be between 50 and 500 characters")
        String reason) {}
