package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetUpStyleFactories.createCustomStyleTest;
import static com.wcc.platform.factories.SetupFactories.createHeroSectionTest;

import com.wcc.platform.domain.cms.attributes.CommonSection;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.attributes.ListSection;
import com.wcc.platform.domain.cms.pages.programme.ProgrammeCardItem;
import com.wcc.platform.domain.cms.pages.programme.ProgrammesPage;
import java.util.List;

public class SetUpProgrammesFactories {

  public static ProgrammesPage createProgrammesPageTest() {
    return new ProgrammesPage(
        "page:PROGRAMMES",
        createHeroSectionTest(),
        new CommonSection(),
        createProgrammesSectionTest());
  }

  public static ListSection<ProgrammeCardItem> createProgrammesSectionTest() {
    return new ListSection<>("title", "description", null, List.of(createProgrammeCardItemTest()));
  }

  public static ProgrammeCardItem createProgrammeCardItemTest() {
    return new ProgrammeCardItem(
        "Code Club",
        "Code club is a series of short workshops that teach basic coding skills.",
        List.of(
            new Image(
                "https://drive.google.com/uc?id=1efbBcw8yaASbSx3pgqcj06tIN-P2Wf55&export=download",
                "There is a group of women showing WCC logo",
                ImageType.DESKTOP)),
        new LabelLink("programmes page", "programmes-page", "/programmes/code-club"),
        createCustomStyleTest());
  }
}
