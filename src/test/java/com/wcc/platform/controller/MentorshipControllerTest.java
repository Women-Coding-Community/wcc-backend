package com.wcc.platform.controller;

import static com.wcc.platform.factories.MockMvcRequestFactory.getRequest;
import static com.wcc.platform.factories.MockMvcRequestFactory.postRequest;
import static com.wcc.platform.factories.SetupMenteeFactories.createMenteeTest;
import static com.wcc.platform.factories.SetupMentorFactories.createMentorTest;
import static com.wcc.platform.factories.SetupMentorFactories.createUpdatedMentorTest;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.domain.exceptions.MemberNotFoundException;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.service.MenteeService;
import com.wcc.platform.service.MentorshipService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/** Unit test for mentorship APIs. */
@ActiveProfiles("test")
@Import({SecurityConfig.class, TestConfig.class})
@WebMvcTest(MentorshipController.class)
class MentorshipControllerTest {

  private static final String API_MENTORS = "/api/platform/v1/mentors";
  private static final String API_MENTEES = "/api/platform/v1/mentees";
  private static final String API_KEY_HEADER = "X-API-KEY";
  private static final String API_KEY_VALUE = "test-api-key";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private MockMvc mockMvc;
  @MockBean private MentorshipService mentorshipService;
  @MockBean private MenteeService menteeService;

  @Test
  void testGetAllMentorsReturnsOk() throws Exception {
    List<MentorDto> mockMentors = List.of(createMentorTest("Jane").toDto());
    when(mentorshipService.getAllMentors()).thenReturn(mockMentors);

    mockMvc
        .perform(getRequest(API_MENTORS).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].fullName", is("Jane")));
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
  void testCreateMenteeReturnsCreated() throws Exception {
    Mentee mockMentee = createMenteeTest(2L, "Mark", "mark@test.com");
    var currentYear = java.time.Year.now();

    when(menteeService.saveRegistration(any())).thenReturn(mockMentee);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(API_MENTEES)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON)
                .content(
                    "{\"mentee\":{\"id\":2,\"fullName\":\"Mark\",\"email\":\"mark@test.com\",\"position\":\"Software Engineer\",\"slackDisplayName\":\"mark-slack\",\"country\":{\"countryCode\":\"US\",\"countryName\":\"USA\"},\"city\":\"New York\",\"companyName\":\"Tech Corp\",\"images\":[],\"network\":[],\"profileStatus\":\"ACTIVE\",\"bio\":\"Mentee bio\",\"skills\":{\"yearsExperience\":2,\"areas\":[\"BACKEND\"],\"languages\":[\"JAVASCRIPT\"],\"mentorshipFocus\":[\"GROW_BEGINNER_TO_MID\"]}},\"mentorshipType\":\"AD_HOC\",\"cycleYear\":\""
                        + currentYear
                        + "\",\"applications\":[{\"menteeId\":null,\"mentorId\":1,\"priorityOrder\":1}]}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(2)))
        .andExpect(jsonPath("$.fullName", is("Mark")));
  }

  @Test
  void testUpdateMentorReturnsOk() throws Exception {
    Long mentorId = 1L;
    Mentor existingMentor = createMentorTest();
    MentorDto mentorDto = createMentorTest().toDto();
    Mentor updatedMentor = createUpdatedMentorTest(existingMentor, mentorDto);

    when(mentorshipService.updateMentor(eq(mentorId), any(MentorDto.class)))
        .thenReturn(updatedMentor);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(API_MENTORS + "/" + mentorId)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mentorDto)))
        .andExpect(status().isOk());
  }

  @Test
  void testUpdateMentorReturnsUpdatedFields() throws Exception {
    Long mentorId = 1L;
    Mentor existingMentor = createMentorTest();
    MentorDto mentorDto = createMentorTest().toDto();
    Mentor updatedMentor = createUpdatedMentorTest(existingMentor, mentorDto);

    when(mentorshipService.updateMentor(eq(mentorId), any(MentorDto.class)))
        .thenReturn(updatedMentor);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(API_MENTORS + "/" + mentorId)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mentorDto)))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.bio", is(updatedMentor.getBio())))
        .andExpect(jsonPath("$.spokenLanguages", hasSize(2)))
        .andExpect(jsonPath("$.spokenLanguages[0]", is(updatedMentor.getSpokenLanguages().get(0))))
        .andExpect(jsonPath("$.spokenLanguages[1]", is(updatedMentor.getSpokenLanguages().get(1))))
        .andExpect(
            jsonPath("$.skills.yearsExperience", is(updatedMentor.getSkills().yearsExperience())))
        .andExpect(jsonPath("$.skills.areas", hasSize(1)))
        .andExpect(
            jsonPath("$.skills.areas[0]", is(updatedMentor.getSkills().areas().get(0).toString())))
        .andExpect(jsonPath("$.skills.languages", hasSize(2)))
        .andExpect(
            jsonPath(
                "$.skills.languages[0]",
                is(updatedMentor.getSkills().languages().get(0).toString())))
        .andExpect(
            jsonPath(
                "$.skills.languages[1]",
                is(updatedMentor.getSkills().languages().get(1).toString())))
        .andExpect(
            jsonPath(
                "$.menteeSection.idealMentee", is(updatedMentor.getMenteeSection().idealMentee())))
        .andExpect(
            jsonPath(
                "$.menteeSection.additional", is(updatedMentor.getMenteeSection().additional())))
        .andExpect(jsonPath("$.menteeSection.adHoc", hasSize(1)))
        .andExpect(
            jsonPath(
                "$.menteeSection.adHoc[0].month",
                is(updatedMentor.getMenteeSection().adHoc().get(0).month().toString())));
  }

  @Test
  void testUpdateNonExistentMentorThrowsException() throws Exception {
    Long nonExistentMentorId = 999L;
    MentorDto mentorDto = createMentorTest().toDto();

    when(mentorshipService.updateMentor(eq(nonExistentMentorId), any(MentorDto.class)))
        .thenThrow(new MemberNotFoundException(nonExistentMentorId));

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(API_MENTORS + "/" + nonExistentMentorId)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mentorDto)))
        .andExpect(status().isNotFound());
  }
}
