package com.wcc.platform.controller;

import static com.wcc.platform.factories.MockMvcRequestFactory.getRequest;
import static com.wcc.platform.factories.MockMvcRequestFactory.postRequest;
import static com.wcc.platform.factories.SetupFactories.createMemberDtoTest;
import static com.wcc.platform.factories.SetupFactories.createMemberTest;
import static com.wcc.platform.factories.SetupFactories.createUpdatedMemberTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.service.MemberService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/** Unit test for members APIs. */
@ActiveProfiles("test")
@Import({SecurityConfig.class, TestConfig.class})
@WebMvcTest(MemberController.class)
class MemberControllerTest {

  private static final String API_MEMBERS = "/api/platform/v1/members";
  private static final String API_KEY_HEADER = "X-API-KEY";
  private static final String API_KEY_VALUE = "test-api-key";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private MockMvc mockMvc;
  @MockBean private MemberService memberService;

  @Test
  void testGetAllMembersReturnsOk() throws Exception {
    List<Member> mockMembers =
        List.of(createMemberTest(MemberType.MEMBER), createMemberTest(MemberType.VOLUNTEER));
    when(memberService.getAllMembers()).thenReturn(mockMembers);

    mockMvc
        .perform(getRequest(API_MEMBERS).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)));
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
                .contentType(APPLICATION_JSON)
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

  @Test
  void testCreateMemberWithIsWomenNonBinaryReturnsFieldInResponse() throws Exception {
    Member member = createMemberTest(MemberType.MEMBER);
    member =
        member.toBuilder()
            .isWomenNonBinary(true)
            .build();
    when(memberService.createMember(any(Member.class))).thenReturn(member);

    mockMvc
        .perform(postRequest(API_MEMBERS, member))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.isWomenNonBinary", is(true)));
  }

  @Test
  void testGetAllMembersIncludesIsWomenNonBinaryField() throws Exception {
    Member member1 = createMemberTest(MemberType.MEMBER);
    member1 = member1.toBuilder().isWomenNonBinary(true).build();

    Member member2 = createMemberTest(MemberType.VOLUNTEER);
    member2 = member2.toBuilder().isWomenNonBinary(false).build();

    List<Member> mockMembers = List.of(member1, member2);
    when(memberService.getAllMembers()).thenReturn(mockMembers);

    mockMvc
        .perform(getRequest(API_MEMBERS).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].isWomenNonBinary", is(true)))
        .andExpect(jsonPath("$[1].isWomenNonBinary", is(false)));
  }
}
