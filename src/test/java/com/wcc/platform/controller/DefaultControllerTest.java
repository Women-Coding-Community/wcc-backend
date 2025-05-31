package com.wcc.platform.controller;

import static com.wcc.platform.domain.platform.SocialNetworkType.SLACK;
import static com.wcc.platform.factories.MockMvcRequestFactory.getRequest;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.domain.cms.attributes.CmsIcon;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.attributes.ListSection;
import com.wcc.platform.domain.cms.pages.FooterSection;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.domain.cms.pages.programme.ProgrammeItem;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.Event;
import com.wcc.platform.domain.platform.ProgramType;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.factories.SetupEventFactories;
import com.wcc.platform.factories.SetupFactories;
import com.wcc.platform.factories.SetupMentorshipFactories;
import com.wcc.platform.factories.SetupProgrammeFactories;
import com.wcc.platform.service.CmsService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Unit test for footer api. */
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(DefaultController.class)
class DefaultControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockBean private CmsService service;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void testInternalServerErrorFooter() throws Exception {
    when(service.getFooter())
        .thenThrow(new PlatformInternalException("Invalid Json", new RuntimeException()));

    mockMvc
        .perform(getRequest("/api/cms/v1/footer").contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("Invalid Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/footer")));
  }

  @Test
  void testOkResponseFooter() throws Exception {
    when(service.getFooter())
        .thenReturn(
            new FooterSection(
                "",
                "footer_title",
                "footer_subtitle",
                "footer_desc",
                List.of(new SocialNetwork(SLACK, "slack_link")),
                new LabelLink("label_title", "label", "label_uri")));

    mockMvc
        .perform(getRequest("/api/cms/v1/footer").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is("footer_title")))
        .andExpect(jsonPath("$.subtitle", is("footer_subtitle")))
        .andExpect(jsonPath("$.description", is("footer_desc")))
        .andExpect(jsonPath("$.network[0].type", is("slack")))
        .andExpect(jsonPath("$.network[0].link", is("slack_link")))
        .andExpect(jsonPath("$.link.title", is("label_title")))
        .andExpect(jsonPath("$.link.label", is("label")))
        .andExpect(jsonPath("$.link.uri", is("label_uri")));
  }

  @Test
  void testLandingPageInternalServerError() throws Exception {
    when(service.getLandingPage())
        .thenThrow(new PlatformInternalException("Invalid Json", new RuntimeException()));

    mockMvc
        .perform(getRequest("/api/cms/v1/landingPage").contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("Invalid Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/landingPage")));
  }

  @Test
  void testLandingPageOkResponse() throws Exception {
    var page =
        LandingPage.builder()
            .heroSection(SetupFactories.createHeroSectionTest())
            .programmes(createSectionProgramme())
            .events(createSectionEvent("Events", ProgramType.TECH_TALK))
            .announcements(createSectionEvent("Announcements", ProgramType.OTHERS))
            .feedbackSection(SetupMentorshipFactories.createFeedbackSectionTest())
            .partners(SetupFactories.createListSectionPartnerTest())
            .build();

    when(service.getLandingPage()).thenReturn(page);

    mockMvc
        .perform(getRequest("/api/cms/v1/landingPage").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(page)));
  }

  private ListSection<Event> createSectionEvent(final String title, final ProgramType techTalk) {
    return new ListSection<>(
        title,
        "check our latest " + techTalk,
        SetupFactories.createLinkTest(),
        List.of(SetupEventFactories.createEventTest(techTalk)));
  }

  private ListSection<ProgrammeItem> createSectionProgramme() {
    return new ListSection<>(
        "Programmes ",
        "Description Programme",
        null,
        List.of(
            SetupProgrammeFactories.createProgrammeItemsTest(
                ProgramType.MACHINE_LEARNING, CmsIcon.DIVERSITY),
            SetupProgrammeFactories.createProgrammeItemsTest(ProgramType.BOOK_CLUB, CmsIcon.BOOK),
            SetupProgrammeFactories.createProgrammeItemsTest(ProgramType.TECH_TALK, CmsIcon.WORK),
            SetupProgrammeFactories.createProgrammeItemsTest(
                ProgramType.WRITING_CLUB, CmsIcon.CALENDAR)));
  }
}
