package com.wcc.platform.service;

import com.wcc.platform.domain.LeadershipMember;
import com.wcc.platform.domain.SocialNetwork;
import com.wcc.platform.domain.pages.Page;
import com.wcc.platform.domain.pages.TeamPage;
import com.wcc.platform.domain.pages.attributes.Contact;
import com.wcc.platform.domain.pages.attributes.Image;
import com.wcc.platform.domain.pages.attributes.MemberByType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.wcc.platform.domain.SocialNetworkType.*;
import static com.wcc.platform.domain.pages.attributes.ImageType.DESKTOP;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CmsServiceIntegrationTest {

    @Autowired
    private CmsService service;

    @Test
    void getTeamPageTest() {
        var result = service.getTeam();

        var page = new Page("title", "subtitle", "description");
        var links = List.of(new SocialNetwork(EMAIL, "london@wcc.com"),
                new SocialNetwork(SLACK, "http://shortlink_to_slack.com"));
        var contact = new Contact("Contact us", links);
        var images = List.of(new Image("image.png", "director alt", DESKTOP));
        var network = List.of(new SocialNetwork(LINKEDIN, "https://www.linkedin.com/director"));
        var directors = List.of(new LeadershipMember("Director1", "Senior Software Engineer", null, images, network));
        var membersByType = new MemberByType(directors, List.of(), List.of());

        var pageExpected = new TeamPage(page, contact, membersByType);

        assertEquals(pageExpected.page(), result.page());
        assertEquals(pageExpected.contact(), result.contact());

        assertEquals(directors, result.membersByType().directors());
        assertEquals(1, result.membersByType().leads().size());
        assertEquals(1, result.membersByType().evangelists().size());
    }
}