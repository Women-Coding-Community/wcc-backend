package com.wcc.platform.controller;

import static com.wcc.platform.domain.cms.PageType.ABOUT_US;
import static com.wcc.platform.domain.cms.PageType.CODE_OF_CONDUCT;
import static com.wcc.platform.domain.cms.PageType.PARTNERS;
import static com.wcc.platform.factories.MockMvcRequestFactory.getRequest;
import static com.wcc.platform.factories.SetupFactories.DEFAULT_CURRENT_PAGE;
import static com.wcc.platform.factories.SetupFactories.DEFAULT_PAGE_SIZE;
import static com.wcc.platform.factories.SetupPagesFactories.createCodeOfConductPageTest;
import static com.wcc.platform.factories.SetupPagesFactories.createPartnersPageTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.factories.SetupPagesFactories;
import com.wcc.platform.service.CmsAboutUsService;
import com.wcc.platform.utils.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Unit test for about page apis. */
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(AboutController.class)
class AboutControllerTest {

  private static final String API_CODE_OF_CONDUCT = "/api/cms/v1/code-of-conduct";
  private static final String API_ABOUT_US = "/api/cms/v1/about";
  private static final String API_COLLABORATORS = "/api/cms/v1/collaborators";
  private static final String API_CELEBRATEHER = "/api/cms/v1/celebrateHer";
  private static final String API_PARTNERS = "/api/cms/v1/partners";
  private static final String PAGINATION_COLLABORATORS =
      "?currentPage=" + DEFAULT_CURRENT_PAGE + "&pageSize=" + DEFAULT_PAGE_SIZE;

  @Autowired private MockMvc mockMvc;
  @MockBean private CmsAboutUsService service;

  @Test
  void testPartnersInternalError() throws Exception {
    var internalError = new PlatformInternalException("internal Json", new RuntimeException());
    when(service.getPartners()).thenThrow(internalError);

    mockMvc
        .perform(getRequest(API_PARTNERS).contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("internal Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/partners")));
  }

  @Test
  void testPartnersNotFound() throws Exception {
    when(service.getPartners()).thenThrow(new ContentNotFoundException("Not Found Exception"));

    mockMvc
        .perform(getRequest(API_PARTNERS).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Not Found Exception")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/partners")));
  }

  @Test
  void testPartnersOkResponse() throws Exception {
    var fileName = PARTNERS.getFileName();
    var expectedJson = FileUtil.readFileAsString(fileName);

    when(service.getPartners()).thenReturn(createPartnersPageTest(fileName));

    mockMvc
        .perform(getRequest(API_PARTNERS).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void testTeamNotFound() throws Exception {
    when(service.getTeam()).thenThrow(new ContentNotFoundException("Not Found Exception"));

    mockMvc
        .perform(getRequest("/api/cms/v1/team").contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Not Found Exception")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/team")));
  }

  @Test
  void testTeamInternalError() throws Exception {
    var internalError = new PlatformInternalException("internal error", new RuntimeException());
    when(service.getTeam()).thenThrow(internalError);

    mockMvc
        .perform(getRequest("/api/cms/v1/team").contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("internal error")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/team")));
  }

  @Test
  void testCodeOfConductNotFound() throws Exception {
    when(service.getCodeOfConduct()).thenThrow(new ContentNotFoundException("Not Found Exception"));

    mockMvc
        .perform(getRequest(API_CODE_OF_CONDUCT).contentType(APPLICATION_JSON))
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
        .perform(getRequest(API_CODE_OF_CONDUCT).contentType(APPLICATION_JSON))
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
        .perform(getRequest(API_CODE_OF_CONDUCT).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void testAboutUsNotFound() throws Exception {
    when(service.getAboutUs()).thenThrow(new ContentNotFoundException("Not Found Exception"));

    mockMvc
        .perform(getRequest(API_ABOUT_US).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Not Found Exception")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/about")));
  }

  @Test
  void testAboutUsInternalError() throws Exception {
    var internalError = new PlatformInternalException("internal Json", new RuntimeException());
    when(service.getAboutUs()).thenThrow(internalError);

    mockMvc
        .perform(getRequest(API_ABOUT_US).contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("internal Json")))
        .andExpect(jsonPath("$.details", is("uri=" + API_ABOUT_US)));
  }

  @Test
  void testAboutUsOkResponse() throws Exception {
    var fileName = ABOUT_US.getFileName();
    var expectedJson = FileUtil.readFileAsString(fileName);

    when(service.getAboutUs()).thenReturn(SetupPagesFactories.createAboutUsPageTest(fileName));

    mockMvc
        .perform(getRequest(API_ABOUT_US).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }
}
