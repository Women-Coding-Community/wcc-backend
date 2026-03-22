package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for mentor accepting an application.
 */
public record ApplicationAcceptRequest(
    @Size(max = 500, message = "Response message cannot exceed 500 characters")
    String mentorResponse
) {
}
