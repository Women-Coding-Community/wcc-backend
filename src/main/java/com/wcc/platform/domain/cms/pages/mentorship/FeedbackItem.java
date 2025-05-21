package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.platform.MemberType;
import jakarta.validation.constraints.NotBlank;
import java.time.Year;
import java.util.Date;

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
    MemberType memberType,
    Year year,
    Date date,
    String rating,
    String type) {}
