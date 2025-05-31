package com.wcc.platform.controller;

import static com.wcc.platform.domain.cms.PageType.PARTNERS;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.factories.MockMvcRequestFactory;
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
class CmsControllerTest {

  private static final String API_PARTNERS = "/api/cms/v1/partners";
  @Autowired private MockMvc mockMvc;
  @MockBean private CmsAboutUsService service;

  @Test
  void testPartnersInternalError() throws Exception {
    var internalError = new PlatformInternalException("internal Json", new RuntimeException());
    when(service.getPartners()).thenThrow(internalError);

    mockMvc
        .perform(MockMvcRequestFactory.getRequest(API_PARTNERS).contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("internal Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/partners")));
  }

  @Test
  void testPartnersNotFound() throws Exception {
    when(service.getPartners()).thenThrow(new ContentNotFoundException("Not Found Exception"));

    mockMvc
        .perform(MockMvcRequestFactory.getRequest(API_PARTNERS).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Not Found Exception")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/partners")));
  }

  @Test
  void testPartnersOkResponse() throws Exception {
    var fileName = PARTNERS.getFileName();
    var expectedJson = FileUtil.readFileAsString(fileName);

    when(service.getPartners()).thenReturn(SetupPagesFactories.createPartnersPageTest(fileName));

    mockMvc
        .perform(MockMvcRequestFactory.getRequest(API_PARTNERS).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }
}
