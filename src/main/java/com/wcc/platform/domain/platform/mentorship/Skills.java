package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.validation.annotation.Validated;

/**
 * Skills of the mentor.
 *
 * @param yearsExperience number of years of experience
 * @param areas technical areas with proficiency levels
 * @param languages programming languages with proficiency levels
 * @param mentorshipFocus mentorship focus areas like Grow from beginner to mid-level
 */
@Validated
public record Skills(
    @NotNull(message = "Years of experience is required. It can be `0`") Integer yearsExperience,
    @NotEmpty(message = "At least one technical area must be provided")
        List<TechnicalAreaProficiency> areas,
    List<LanguageProficiency> languages,
    @NotEmpty(message = "At least one mentorship focus area must be provided")
        List<MentorshipFocusArea> mentorshipFocus) {}
