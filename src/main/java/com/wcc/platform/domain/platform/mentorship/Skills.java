package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Skills of the mentor
 *
 * @param yearsExperience number of years of experience
 * @param areas technical areas
 * @param languages programming languages
 */
public record Skills(
    @NotNull Integer yearsExperience,
    @NotNull List<TechnicalArea> areas,
    @NotNull List<Languages> languages) {}
