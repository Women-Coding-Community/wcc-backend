package com.wcc.platform.domain.cms.pages.mentorship;

import com.wcc.platform.domain.platform.StudyGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Record for the section of study groups in the Study Groups page. */
public record StudyGroupSection(@NotBlank String title, @NotEmpty List<StudyGroup> studyGroups) {}
