package com.wcc.platform.domain.cms.pages.mentorship;

import java.util.List;

public record FeedbackSection(String title, List<FeedbackItem> feedbacks) {
}
