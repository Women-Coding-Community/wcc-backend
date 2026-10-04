package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/** Request payload for creating a mentorship cycle. */
public record MentorshipCycleCreateRequest(
    @NotNull Integer cycleYear,
    @NotNull @Min(1) @Max(12) Integer cycleMonth,
    @NotNull MentorshipType mentorshipType,
    @NotNull LocalDate registrationStartDate,
    @NotNull LocalDate registrationEndDate,
    @NotNull LocalDate cycleStartDate,
    LocalDate cycleEndDate,
    @Min(1) Integer maxMenteesPerMentor,
    String description) {}
