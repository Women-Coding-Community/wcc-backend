package com.wcc.platform.factories;

import com.wcc.platform.domain.cms.attributes.CmsIcon;
import com.wcc.platform.domain.cms.pages.programme.ProgrammeItem;
import com.wcc.platform.domain.platform.ProgramType;

/** Test factories for programme. */
public class SetupProgrammeFactories {

  /** Test factory. * */
  public static ProgrammeItem createProgrammeItemTest(ProgramType type, CmsIcon icon) {
    return new ProgrammeItem(type, SetupFactories.createSimpleLinkTest(), icon);
  }
}
