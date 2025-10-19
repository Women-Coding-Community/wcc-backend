package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import java.util.List;

/** Applied Filters for mentors page. */
public record MentorAppliedFilters(
    String keyword,
    List<MentorshipType> mentorshipTypes,
    Integer yearsExperience,
    List<TechnicalArea> areas,
    List<Languages> languages,
    List<MentorshipFocusArea> focus) {}
