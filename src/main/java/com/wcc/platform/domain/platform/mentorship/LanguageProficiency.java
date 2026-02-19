package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.CodeLanguage;
import com.wcc.platform.domain.cms.attributes.ProficiencyLevel;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a programming language and the proficiency level of a mentor in that language.
 *
 * @param language the programming language
 * @param proficiencyLevel the proficiency level in the language
 */
public record LanguageProficiency(
    @NotNull CodeLanguage language, @NotNull ProficiencyLevel proficiencyLevel) {}
