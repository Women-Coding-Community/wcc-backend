package com.wcc.platform.domain.cms.pages.mentorship;

import java.util.List;

/**
 * CMS Feedback Section.
 *
 * @param title title to be shown in the feedback section
 * @param feedbacks list of all feedbacks
 */
public record FeedbackSection(String title, List<FeedbackItem> feedbacks) {}
