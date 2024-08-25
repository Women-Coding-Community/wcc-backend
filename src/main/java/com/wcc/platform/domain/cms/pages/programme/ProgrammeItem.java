package com.wcc.platform.domain.cms.pages.programme;

import com.wcc.platform.domain.cms.attributes.CmsIcon;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.platform.ProgramType;

/** Programme item to be listed in the landing page. */
public record ProgrammeItem(ProgramType name, LabelLink link, CmsIcon icon) {}
