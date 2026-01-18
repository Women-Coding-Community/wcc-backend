package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) representing a mentee application for mentorship matching purposes.
 * This record encapsulates the details required to link a mentee with a mentor, along with the
 * priority order of the application.
 *
 * @param menteeId Unique identifier of the mentee applying for mentorship.
 * @param mentorId Unique identifier of the mentor to whom the application is directed.
 * @param priorityOrder Priority order of the application, ranging from 1 (highest priority) to 5
 *     (lowest priority).
 */
public record MenteeApplicationDto(
    @NotNull Long menteeId,
    @NotNull Long mentorId,
    @NotNull @Min(1) @Max(5) Integer priorityOrder) {}
