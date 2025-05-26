package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.platform.Mentor;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * CMS Mentors Page details.
 *
 * @param heroSection hero section to be shown on mentors page
 * @param mentors section to highlight why you apply to a become a mentor
 */
public record MentorsPage(
    @NotNull String id, @NotNull HeroSection heroSection, @NotNull List<Mentor> mentors) {}
