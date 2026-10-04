package com.wcc.platform.domain.platform.mentorship;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

/** Request payload for creating a mentorship cycle. */
public record MentorshipCycleCreateRequest(
    @NotNull @FutureOrPresent Year cycleYear,
    @NotNull Month cycleMonth,
    @NotNull MentorshipType mentorshipType,
    @NotNull @FutureOrPresent LocalDate registrationStartDate,
    @NotNull @FutureOrPresent LocalDate registrationEndDate,
    @NotNull @FutureOrPresent LocalDate cycleStartDate,
    @FutureOrPresent LocalDate cycleEndDate,
    @Min(1) Integer maxMenteesPerMentor,
    String description) {}
