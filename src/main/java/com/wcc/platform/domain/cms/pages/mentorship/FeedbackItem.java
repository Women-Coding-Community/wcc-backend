package com.wcc.platform.domain.cms.pages.mentorship;

import java.time.Year;

public record FeedbackItem(String name, String feedback, boolean mentee, Year year) {
}
