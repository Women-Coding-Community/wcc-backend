package com.wcc.platform.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.service.PlatformService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/** Unit test for members pages apis. */
@WebMvcTest(MemberController.class)
public class MemberControllerTest {
  private static final String API_MEMBERS = "/api/platform/v1/members";

  @Autowired private MockMvc mockMvc;
  @MockBean private PlatformService service;

  @Test
  void testMembersNotFound() throws Exception {
    when(service.getAll()).thenThrow(new ContentNotFoundException("Not Found Exception"));

    mockMvc
        .perform(get(API_MEMBERS).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Not Found Exception")))
        .andExpect(jsonPath("$.details", is("uri=/api/platform/v1/members")));
  }

  @Test
  void testMembersInternalError() throws Exception {
    var internalError = new PlatformInternalException("internal Json", new RuntimeException());
    when(service.getAll()).thenThrow(internalError);

    mockMvc
        .perform(get(API_MEMBERS).contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("internal Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/platform/v1/members")));
  }

  @Test
  void testMembersOkResponse() {}
}
