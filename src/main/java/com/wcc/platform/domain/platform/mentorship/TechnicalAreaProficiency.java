package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.ProficiencyLevel;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a technical area and the proficiency level of a mentor/mentee in that area.
 *
 * @param technicalArea the technical area
 * @param proficiencyLevel the proficiency level in the technical area
 */
public record TechnicalAreaProficiency(
    @NotNull TechnicalArea technicalArea, @NotNull ProficiencyLevel proficiencyLevel) {}
