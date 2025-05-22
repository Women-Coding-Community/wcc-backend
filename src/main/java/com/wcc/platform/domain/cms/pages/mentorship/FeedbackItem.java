package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.platform.MemberType;
import jakarta.validation.constraints.NotBlank;
import java.time.Year;

/** Community members and partners Feedbacks. */
public record FeedbackItem(
    @NotBlank String name,
    @NotBlank String feedback,
    MemberType memberType,
    Year year,
    String date,
    String rating,
    String type) {}
