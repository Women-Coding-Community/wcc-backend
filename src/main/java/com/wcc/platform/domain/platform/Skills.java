package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Experience;
import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import java.util.List;

/**
 * Skills of the mentor
 *
 * @param experienceRange experience level
 * @param areas technical areas
 * @param languages programming languages
 */
public record Skills(
    Integer yearsExperience,
    Experience experienceRange,
    List<TechnicalArea> areas,
    List<Languages> languages) {}
