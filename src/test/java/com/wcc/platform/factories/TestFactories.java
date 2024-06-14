package com.wcc.platform.factories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wcc.platform.domain.cms.attributes.*;
import com.wcc.platform.domain.cms.pages.FooterPage;
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
        var team = new LeadershipMember();

        team.setFullName("fullName " + type.name());
        team.setPosition("position " + type.name());
        team.setMemberType(type);
        team.setImages(List.of(createImageTest()));
        team.setNetwork(List.of(createSocialNetworkTest()));

        return team;
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

    public static FooterPage createFooterPageTest() {
        return new FooterPage("footer_title", "footer_subtitle", "footer_description", createNetworks(), createLabelLink());
    }

    public static FooterPage createFooterPageTest(String fileName) {
        try {
            String content = FileUtil.readFileAsString(fileName);
            return ObjectMapperTestFactory.getInstance().readValue(content, FooterPage.class);
        } catch (JsonProcessingException e) {
            return createFooterPageTest();
        }
    }

    public static List<Network> createNetworks() {
        return List.of(new Network("type1", "link1"), new Network("type2", "link2"));
    }

    public static LabelLink createLabelLink() {
        return new LabelLink("link_title", "link_label", "link_uri");
    }


}
