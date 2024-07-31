package com.wcc.platform.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.cms.attributes.Network;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.service.CmsService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/** Unit test for footer api. */
@WebMvcTest(DefaultController.class)
public class DefaultControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockBean private CmsService mockCmsService;

  @Test
  void testInternalServerError() throws Exception {
    when(mockCmsService.getFooter())
        .thenThrow(new PlatformInternalException("Invalid Json", new RuntimeException()));

    mockMvc
        .perform(get("/api/cms/v1/footer").contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("Invalid Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/footer")));
  }

  @Test
  void testOkResponse() throws Exception {
    when(mockCmsService.getFooter())
        .thenReturn(
            new FooterPage(
                "footer_title",
                "footer_subtitle",
                "footer_desc",
                List.of(new Network("net_type", "net_link")),
                new LabelLink("label_title", "label", "label_uri")));

    mockMvc
        .perform(get("/api/cms/v1/footer").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is("footer_title")))
        .andExpect(jsonPath("$.subtitle", is("footer_subtitle")))
        .andExpect(jsonPath("$.description", is("footer_desc")))
        .andExpect(jsonPath("$.network[0].type", is("net_type")))
        .andExpect(jsonPath("$.network[0].link", is("net_link")))
        .andExpect(jsonPath("$.link.title", is("label_title")))
        .andExpect(jsonPath("$.link.label", is("label")))
        .andExpect(jsonPath("$.link.uri", is("label_uri")));
  }
}
