package com.wcc.platform.controller;

import static com.wcc.platform.factories.MockMvcRequestFactory.getRequest;
import static com.wcc.platform.factories.MockMvcRequestFactory.postRequest;
import static com.wcc.platform.factories.SetupFactories.createMemberDtoTest;
import static com.wcc.platform.factories.SetupFactories.createMemberTest;
import static com.wcc.platform.factories.SetupFactories.createUpdatedMemberTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.service.MemberService;
import com.wcc.platform.service.MentorshipService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/** Unit test for members and mentors APIs. */
@ActiveProfiles("test")
@Import({SecurityConfig.class, TestConfig.class})
@WebMvcTest(MemberController.class)
class MemberControllerTest {

  private static final String API_MEMBERS = "/api/platform/v1/members";
  private static final String API_MENTORS = "/api/platform/v1/mentors";
  private static final String API_KEY_HEADER = "X-API-KEY";
  private static final String API_KEY_VALUE = "test-api-key";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private MockMvc mockMvc;
  @MockBean private MemberService memberService;
  @MockBean private MentorshipService mentorshipService;

  @Test
  void testGetAllMembersReturnsOk() throws Exception {
    List<Member> mockMembers =
        List.of(createMemberTest(MemberType.MEMBER), createMemberTest(MemberType.VOLUNTEER));
    when(memberService.getAllMembers()).thenReturn(mockMembers);

    mockMvc
        .perform(getRequest(API_MEMBERS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)));
  }

  @Test
  void testGetAllMentorsReturnsOk() throws Exception {
    List<MentorDto> mockMentors = List.of(createMentorTest("Jane").toDto());
    when(mentorshipService.getAllMentors()).thenReturn(mockMentors);

    mockMvc
        .perform(getRequest(API_MENTORS).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].fullName", is("Jane")));
  }

  @Test
  void testCreateMemberReturnsCreated() throws Exception {
    Member member = createMemberTest(MemberType.MEMBER);
    when(memberService.createMember(any(Member.class))).thenReturn(member);

    mockMvc
        .perform(postRequest(API_MEMBERS, member))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.email", is("member@wcc.com")))
        .andExpect(jsonPath("$.fullName", is("fullName MEMBER")));
  }

  @Test
  void testCreateMentorReturnsCreated() throws Exception {
    var mentor = createMentorTest("Jane");
    when(mentorshipService.create(any(Mentor.class))).thenReturn(mentor);

    mockMvc
        .perform(postRequest(API_MENTORS, mentor))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.fullName", is("Jane")));
  }

  @Test
  void testUpdateMemberReturnsOk() throws Exception {
    Long memberId = 1L;
    Member existingMember = createMemberTest(MemberType.COLLABORATOR);
    MemberDto memberDto = createMemberDtoTest(MemberType.COLLABORATOR);
    Member updated = createUpdatedMemberTest(existingMember, memberDto);
    when(memberService.updateMember(eq(memberId), any(MemberDto.class))).thenReturn(updated);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(API_MEMBERS + "/" + memberId)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.fullName", is(updated.getFullName())))
        .andExpect(jsonPath("$.position", is(updated.getPosition())));
  }

  @Test
  void testDeleteMemberReturnsNoContent() throws Exception {
    Long memberId = 1L;
    doNothing().when(memberService).deleteMember(memberId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(API_MEMBERS + "/" + memberId)
                .header(API_KEY_HEADER, API_KEY_VALUE))
        .andExpect(status().isNoContent());

    verify(memberService).deleteMember(memberId);
  }
}
