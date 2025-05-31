package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.HeroSection;
import com.wcc.platform.domain.cms.attributes.ListSection;
import com.wcc.platform.domain.cms.attributes.style.CustomStyle;
import com.wcc.platform.domain.platform.StudyGroup;
import jakarta.validation.constraints.NotNull;

/** Represents the Study Group page. */
public record MentorshipStudyGroupsPage(
    @NotNull String id,
    @NotNull HeroSection heroSection,
    @NotNull CommonSection section,
    @NotNull Contact contact,
    @NotNull ListSection<StudyGroup> studyGroupSection,
    @NotNull CustomStyle customStyle) {}
