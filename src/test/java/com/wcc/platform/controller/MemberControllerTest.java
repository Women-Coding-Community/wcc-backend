package com.wcc.platform.controller;

import static com.wcc.platform.domain.cms.PageType.CELEBRATE_HER;
import static com.wcc.platform.domain.cms.PageType.COLLABORATOR;
import static com.wcc.platform.factories.SetupFactories.DEFAULT_CURRENT_PAGE;
import static com.wcc.platform.factories.SetupFactories.DEFAULT_PAGE_SIZE;
import static com.wcc.platform.factories.SetupPagesFactories.createCelebrateHerPageTest;
import static com.wcc.platform.factories.SetupPagesFactories.createCollaboratorPageTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.factories.MockMvcRequestFactory;
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
class MemberControllerTest {

  private static final String API_COLLABORATORS = "/api/cms/v1/collaborators";
  private static final String API_CELEBRATE_HER = "/api/cms/v1/celebrateHer";
  private static final String PAGINATION_COLLABORATORS =
      "?currentPage=" + DEFAULT_CURRENT_PAGE + "&pageSize=" + DEFAULT_PAGE_SIZE;

  @Autowired private MockMvc mockMvc;
  @MockBean private CmsAboutUsService service;

  @Test
  void testCollaboratorNotFound() throws Exception {
    when(service.getCollaborator(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE))
        .thenThrow(new ContentNotFoundException("Not Found Exception"));

    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest("/api/cms/v1/collaborators")
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Not Found Exception")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/collaborators")));
  }

  @Test
  void testCollaboratorInternalError() throws Exception {
    var internalError = new PlatformInternalException("internal Json", new RuntimeException());
    when(service.getCollaborator(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE)).thenThrow(internalError);

    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest("/api/cms/v1/collaborators")
                .contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("internal Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/collaborators")));
  }

  @Test
  void testCollaboratorOkResponse() throws Exception {
    var fileName = COLLABORATOR.getFileName();
    var expectedJson = FileUtil.readFileAsString(fileName);

    when(service.getCollaborator(anyInt(), anyInt()))
        .thenReturn(createCollaboratorPageTest(fileName));

    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest(
                    String.format("%s%s", API_COLLABORATORS, PAGINATION_COLLABORATORS))
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void testCelebrateHerOkResponse() throws Exception {
    var fileName = CELEBRATE_HER.getFileName();
    var expectedJson = FileUtil.readFileAsString(fileName);

    when(service.getCelebrateHer()).thenReturn(createCelebrateHerPageTest(fileName));

    mockMvc
        .perform(MockMvcRequestFactory.getRequest(API_CELEBRATE_HER).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }
}
