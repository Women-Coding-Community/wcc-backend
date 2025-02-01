package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.PageSection;
import com.wcc.platform.domain.cms.pages.Page;
import jakarta.validation.constraints.NotNull;

/**
 * CMS Mentorship Page Overview details.
 *
 * @param page basic page details
 * @param mentorSection section to highlight why you apply to a become a mentor
 * @param menteeSection section to highlight why you apply to a become a mentee
 * @param feedbackSection section related to mentorship feedbacks
 */
public record MentorshipPage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    @NotNull Page page,
    @NotNull PageSection mentorSection,
    @NotNull PageSection menteeSection,
    FeedbackSection feedbackSection) {}
