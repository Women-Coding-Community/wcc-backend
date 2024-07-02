package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.PageSection;
import com.wcc.platform.domain.cms.pages.Page;

public record MentorshipPage(Page page, PageSection becomeMentorSection, PageSection becomeMenteeSection,
                             FeedbackSection feedbackSection) {
}
