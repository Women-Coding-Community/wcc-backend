package com.wcc.platform.controller;

import static com.wcc.platform.domain.cms.ApiResourcesFile.CODE_OF_CONDUCT;
import static com.wcc.platform.factories.SetupFactories.createCodeOfConductPageTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
import com.wcc.platform.service.CmsService;
import com.wcc.platform.utils.FileUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AboutController.class)
class AboutControllerTest {

  private static final String API_CODE_OF_CONDUCT = "/api/cms/v1/code-of-conduct";

  @Autowired private MockMvc mockMvc;
  @MockBean private CmsService service;

  @Test
  void testNotFound() throws Exception {
    when(service.getTeam()).thenThrow(new ContentNotFoundException("Not Found Exception"));

    mockMvc
        .perform(get("/api/cms/v1/team").contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Not Found Exception")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/team")));
  }

  @Test
  void testInternalError() throws Exception {
    var internalError = new PlatformInternalException("internal error", new RuntimeException());
    when(service.getTeam()).thenThrow(internalError);

    mockMvc
        .perform(get("/api/cms/v1/team").contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("internal error")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/team")));
  }

  @Test
  void testCollaboratorNotFound() throws Exception {
    when(service.getCollaborator()).thenThrow(new ContentNotFoundException("Not Found Exception"));

    mockMvc
        .perform(get("/api/cms/v1/collaborators").contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Not Found Exception")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/collaborators")));
  }

  @Test
  void testCollaboratorInternalError() throws Exception {
    var internalError = new PlatformInternalException("internal Json", new RuntimeException());
    when(service.getCollaborator()).thenThrow(internalError);

    mockMvc
        .perform(get("/api/cms/v1/collaborators").contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("internal Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/collaborators")));
  }

  @Test
  void testCollaboratorOkResponse() throws Exception {
    var collaborator =
        Member.builder()
            .fullName("FullName")
            .position("Position")
            .email("member@wcc.com")
            .slackDisplayName("Slack name")
            .country(new Country("Country code", "Country name"))
            .city("City")
            .jobTitle("Job title")
            .companyName("Company name")
            .memberType(MemberType.COLLABORATOR)
            .images(List.of(new Image("image.png", "alt image", ImageType.DESKTOP)))
            .network(List.of(new SocialNetwork(SocialNetworkType.LINKEDIN, "collaborator_link")))
            .build();

    var collaboratorPage =
        new CollaboratorPage(
            new Page("collaborator_title", "collaborator_subtitle", "collaborator_desc"),
            new Contact(
                "contact_title",
                List.of(new SocialNetwork(SocialNetworkType.LINKEDIN, "page_link"))),
            List.of(collaborator));

    when(service.getCollaborator()).thenReturn(collaboratorPage);

    mockMvc
        .perform(get("/api/cms/v1/collaborators").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.page.title", is("collaborator_title")))
        .andExpect(jsonPath("$.page.subtitle", is("collaborator_subtitle")))
        .andExpect(jsonPath("$.page.description", is("collaborator_desc")))
        .andExpect(jsonPath("$.contact.title", is("contact_title")))
        .andExpect(jsonPath("$.contact.links[0].type", is("LINKEDIN")))
        .andExpect(jsonPath("$.contact.links[0].link", is("page_link")))
        .andExpect(jsonPath("$.collaborators[0].fullName", is("FullName")))
        .andExpect(jsonPath("$.collaborators[0].position", is("Position")))
        .andExpect(jsonPath("$.collaborators[0].email", is("member@wcc.com")))
        .andExpect(jsonPath("$.collaborators[0].slackDisplayName", is("Slack name")))
        .andExpect(jsonPath("$.collaborators[0].country.countryCode", is("Country code")))
        .andExpect(jsonPath("$.collaborators[0].country.countryName", is("Country name")))
        .andExpect(jsonPath("$.collaborators[0].city", is("City")))
        .andExpect(jsonPath("$.collaborators[0].jobTitle", is("Job title")))
        .andExpect(jsonPath("$.collaborators[0].companyName", is("Company name")))
        .andExpect(jsonPath("$.collaborators[0].memberType", is("COLLABORATOR")))
        .andExpect(jsonPath("$.collaborators[0].images[0].path", is("image.png")))
        .andExpect(jsonPath("$.collaborators[0].images[0].alt", is("alt image")))
        .andExpect(jsonPath("$.collaborators[0].images[0].type", is("DESKTOP")))
        .andExpect(jsonPath("$.collaborators[0].network[0].type", is("LINKEDIN")))
        .andExpect(jsonPath("$.collaborators[0].network[0].link", is("collaborator_link")));
  }

  @Test
  void testCodeOfConductNotFound() throws Exception {
    when(service.getCodeOfConduct()).thenThrow(new ContentNotFoundException("Not Found Exception"));

    mockMvc
        .perform(get(API_CODE_OF_CONDUCT).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Not Found Exception")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/code-of-conduct")));
  }

  @Test
  void testCodeOfConductInternalError() throws Exception {
    var internalError = new PlatformInternalException("internal Json", new RuntimeException());
    when(service.getCodeOfConduct()).thenThrow(internalError);

    mockMvc
        .perform(get(API_CODE_OF_CONDUCT).contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("internal Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/code-of-conduct")));
  }

  @Test
  void testCodeOfConductOkResponse() throws Exception {

    var fileName = CODE_OF_CONDUCT.getFileName();
    var expectedJson = FileUtil.readFileAsString(fileName);

    when(service.getCodeOfConduct()).thenReturn(createCodeOfConductPageTest(fileName));

    mockMvc
        .perform(get(API_CODE_OF_CONDUCT).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }
}
