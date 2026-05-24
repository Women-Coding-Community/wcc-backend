package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.NotBlank;

/** Request DTO for confirming a mentorship match. */
public record ConfirmMatchRequest(
    @NotBlank(message = "Google Meet link is required") String googleMeetLink) {}
