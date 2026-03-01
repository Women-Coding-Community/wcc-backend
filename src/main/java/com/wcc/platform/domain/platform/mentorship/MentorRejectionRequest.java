package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request DTO for rejecting a mentor registration. */
public record MentorRejectionRequest(
    @NotBlank(message = "Reason is required when rejecting a mentor")
        @Size(max = 250, message = "Rejection reason cannot exceed 250 characters")
        String reason) {}
