package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Skills of the mentor.
 *
 * @param yearsExperience number of years of experience
 * @param areas technical areas with proficiency levels
 * @param languages programming languages with proficiency levels
 * @param mentorshipFocus mentorship focus areas like Grow from beginner to mid-level
 */
public record Skills(
    @NotNull Integer yearsExperience,
    @NotNull List<TechnicalAreaProficiency> areas,
    @NotNull List<LanguageProficiency> languages,
    @NotNull List<MentorshipFocusArea> mentorshipFocus) {}
