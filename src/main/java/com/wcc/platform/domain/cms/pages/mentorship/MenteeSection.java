package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.platform.MentorshipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Represents the Mentee Section of the Mentorship Page. */
public record MenteeSection(
    @NotBlank List<MentorshipType> mentorshipType,
    Availability availability,
    @NotEmpty String idealMentee,
    List<String> focus,
    String additional) {}
