package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.platform.MemberType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Year;

/**
 * Community members and partners Feedbacks.
 *
 * @param name member's name.
 * @param feedback description of the feedback.
 * @param memberType all available member types in the community.
 * @param year which year the feedback was given.
 */
public record FeedbackItem(
    @NotBlank String name,
    @NotBlank String feedback,
    @NotNull MemberType memberType,
    @NotNull Year year) {}
