package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.style.CustomStyle;
import jakarta.validation.constraints.NotNull;

/** Represents the Study Group page. */
public record MentorshipStudyGroupPage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    @NotNull CommonSection section,
    @NotNull Contact contact,
    @NotNull StudyGroupSection studyGroupSection,
    @NotNull CustomStyle customStyle) {}
