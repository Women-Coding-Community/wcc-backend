package com.wcc.platform.domain.cms.pages.programme;

import com.wcc.platform.domain.cms.attributes.CmsIcon;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.platform.ProgramType;
import jakarta.validation.constraints.NotNull;

/** Programme item to be listed in the landing page. */
public record ProgrammeItem(
    @NotNull ProgramType name, @NotNull LabelLink link, @NotNull CmsIcon icon) {}
