package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.platform.MemberType;
import jakarta.validation.constraints.NotBlank;
import java.time.Year;

/**
 * Community members and partners Feedbacks.
 *
 * @param name member's name.
 * @param feedback description of the feedback.
 * @param memberType all available member types in the community.
 * @param year which year the feedback was given.
 * @param date at what date the feedback was given.
 * @param rating feedback ratings.
 * @param type any specific type of feedback.
 */
public record FeedbackItem(
    @NotBlank String name,
    @NotBlank String feedback,
    MemberType memberType,
    Year year,
    String date,
    String rating,
    String type) {}
