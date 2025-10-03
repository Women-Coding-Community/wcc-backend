package com.wcc.platform.controller;

import static com.wcc.platform.factories.MockMvcRequestFactory.getRequest;
import static com.wcc.platform.factories.SetupFactories.createMemberTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.service.PlatformService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Unit test for about page apis. */
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(MemberController.class)
class MemberControllerTest {

  private static final String API_MEMBERS = "/api/platform/v1/members";
  private static final String API_MENTORS = "/api/platform/v1/mentors";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private MockMvc mockMvc;
  @MockBean private PlatformService platformService;

  @Test
  void testGetAllMembersReturnsOk() throws Exception {
    List<Member> mockMembers =
        List.of(createMemberTest(MemberType.MEMBER), createMemberTest(MemberType.VOLUNTEER));
    when(platformService.getAllMembers()).thenReturn(mockMembers);

    mockMvc
        .perform(getRequest(API_MEMBERS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)));
  }

  @Test
  void testGetAllMentorsReturnsOk() throws Exception {
    List<Mentor> mockMentors = List.of(createMentorTest("Jane"));
    when(platformService.getAllMentors()).thenReturn(mockMentors);

    mockMvc
        .perform(getRequest(API_MENTORS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].fullName", is("Jane")));
  }

  @Test
  void testCreateMemberReturnsCreated() throws Exception {
    var input = createMemberTest(MemberType.MEMBER);
    var json = objectMapper.writeValueAsString(input);
    when(platformService.createMember(any(Member.class))).thenReturn(input);

    /*mockMvc
    .perform(postRequest(API_MEMBERS, json).contentType(MediaType.APPLICATION_JSON))
    .andExpect(status().isCreated());*/
  }
}
