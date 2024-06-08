package com.wcc.platform.factories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.attributes.MemberByType;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.platform.LeadershipMember;
import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.utils.FileUtil;

import java.util.List;

public class TestFactories {

    public static Contact createContactTest() {
        return new Contact("Contact Us", List.of(new SocialNetwork(SocialNetworkType.EMAIL, "test@test.com")));
    }

    public static TeamPage createTeamPageTest() {
        return new TeamPage(createPageTest(), createContactTest(), createMemberByTypeTest());
    }

    public static TeamPage createTeamPageTest(String fileName) {
        try {
            String content = FileUtil.readFileAsString(fileName);
            return ObjectMapperTestFactory.getInstance().readValue(content, TeamPage.class);
        } catch (JsonProcessingException e) {
            return createTeamPageTest();
        }
    }

    public static MemberByType createMemberByTypeTest() {
        var directors = List.of(createMemberTest(MemberType.DIRECTOR));
        var leaders = List.of(createMemberTest(MemberType.LEADER));
        var evangelist = List.of(createMemberTest(MemberType.EVANGELIST));
        return new MemberByType(directors, leaders, evangelist);
    }

    public static Page createPageTest() {
        return new Page("title", "subtitle", "description");
    }

    public static LeadershipMember createMemberTest(MemberType type) {
        return new LeadershipMember("fullName " + type.name(),
                "position " + type.name(), type, List.of(createImageTest()), List.of(createSocialNetworkTest()));
    }

    public static Image createImageTest(ImageType type) {
        return new Image(type + ".png", "alt image" + type, type);
    }

    public static Image createImageTest() {
        return createImageTest(ImageType.MOBILE);
    }

    public static SocialNetwork createSocialNetworkTest(SocialNetworkType type) {
        return new SocialNetwork(type, type + ".com");
    }

    public static SocialNetwork createSocialNetworkTest() {
        return createSocialNetworkTest(SocialNetworkType.INSTAGRAM);
    }

}
