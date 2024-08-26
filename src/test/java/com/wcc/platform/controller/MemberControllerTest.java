package com.wcc.platform.controller;

import static com.wcc.platform.factories.SetupFactories.createMembersTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.service.PlatformService;
import com.wcc.platform.utils.FileUtil;
import java.io.IOException;
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
  @Autowired private ObjectMapper objectMapper;

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
  void testMembersOkResponse() throws Exception {
    var expectedJson = FileUtil.readFileAsString("members/data/members.json");

    when(service.getAll()).thenReturn(createMembersTest("members/data/members.json"));

    mockMvc
        .perform(get(API_MEMBERS).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void testMembersPostNotFound() throws Exception {

    when(service.createMember(any(Member.class)))
        .thenThrow(new ContentNotFoundException("Not Found Exception"));

    mockMvc
        .perform(
            post(API_MEMBERS)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Member.builder().build())))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Not Found Exception")))
        .andExpect(jsonPath("$.details", is("uri=/api/platform/v1/members")));
  }

  @Test
  void testMembersPostInternalError() throws Exception {
    var internalError = new PlatformInternalException("internal Json", new RuntimeException());
    // Member member = createMemberTest(MemberType.MEMBER);
    // when(service.createMember(member)).thenThrow(internalError);

    when(service.createMember(any(Member.class))).thenThrow(internalError);

    mockMvc
        .perform(
            post(API_MEMBERS)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Member.builder().build())))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("internal Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/platform/v1/members")));
  }

  @Test
  void testMembersPostOkResponse() throws Exception {

    Member member = createMembersTest("members/data/members.json").getFirst();

    when(service.createMember(any())).thenReturn(member);

    mockMvc
        .perform(
            post(API_MEMBERS)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(member)))
        .andExpect(status().isOk())
        .andExpect(content().json(memberAsString(member)));
  }

  private String memberAsString(final Member member) {
    try {
      return objectMapper.writeValueAsString(member);
    } catch (IOException e) {
      return Member.builder().build().toString();
    }
  }
}
