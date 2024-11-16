package com.wcc.platform.domain.cms.pages.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * CMS Feedback Section.
 *
 * @param title title to be shown in the feedback section
 * @param feedbacks list of all feedbacks
 */
public record FeedbackSection(@NotBlank String title, @NotEmpty List<FeedbackItem> feedbacks) {}
