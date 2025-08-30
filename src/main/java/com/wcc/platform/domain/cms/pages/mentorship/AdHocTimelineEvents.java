package com.wcc.platform.domain.cms.pages.mentorship;

import jakarta.validation.constraints.NotEmpty;

/** AdHocTimelineMilestone class representing the structure of an ad hoc timeline. */
public record AdHocTimelineEvents(@NotEmpty String title, @NotEmpty String description) {}
