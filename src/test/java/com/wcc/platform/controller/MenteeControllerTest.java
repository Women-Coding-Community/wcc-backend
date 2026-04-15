package com.wcc.platform.controller;

import static com.wcc.platform.factories.SetupMenteeFactories.createMenteeTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.platform.mentorship.ApplicationRejectRequest;
import com.wcc.platform.service.MenteeAdminService;
import com.wcc.platform.service.MenteeService;
import com.wcc.platform.service.MenteeWorkflowService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/** Unit test for MenteeController. */
@ActiveProfiles("test")
@Import({SecurityConfig.class, TestConfig.class})
@WebMvcTest(MenteeController.class)
class MenteeControllerTest {

  private static final String API_MENTEES = "/api/platform/v1/mentees";
  private static final String API_KEY_HEADER = "X-API-KEY";
  private static final String API_KEY_VALUE = "test-api-key";
  private static final String REJECTION_REASON =
      "Application does not meet the eligibility criteria for this mentorship cycle";

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private MenteeService menteeService;
  @MockBean private MenteeAdminService menteeAdminService;
  @MockBean private MenteeWorkflowService menteeWorkflowService;

  @Test
  @DisplayName("Given valid mentee registration, when creating mentee, then return 201 Created")
  void shouldCreateMenteeAndReturnCreated() throws Exception {
    var mockMentee = createMenteeTest(2L, "Mark", "mark@test.com");
    var currentYear = java.time.Year.now();

    when(menteeService.saveRegistration(any())).thenReturn(mockMentee);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(API_MENTEES)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON)
                .content(
                    "{\"mentee\":{\"id\":2,\"fullName\":\"Mark\",\"email\":\"mark@test.com\",\"position\":\"Software Engineer\",\"slackDisplayName\":\"mark-slack\",\"country\":{\"countryCode\":\"US\",\"countryName\":\"USA\"},\"city\":\"New York\",\"companyName\":\"Tech Corp\",\"images\":[],\"network\":[],\"profileStatus\":\"ACTIVE\",\"bio\":\"Mentee bio\",\"skills\":{\"yearsExperience\":2,\"areas\":[{\"technicalArea\":\"BACKEND\",\"proficiencyLevel\":\"BEGINNER\"}],\"languages\":[{\"language\":\"JAVASCRIPT\",\"proficiencyLevel\":\"BEGINNER\"}],\"mentorshipFocus\":[\"GROW_BEGINNER_TO_MID\"]}},\"mentorshipType\":\"AD_HOC\",\"cycleYear\":\""
                        + currentYear
                        + "\",\"applications\":[{\"mentorId\":1,\"priorityOrder\":1,\"whyMentor\":\"This mentor has skills I want to improve\"}]}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(2)))
        .andExpect(jsonPath("$.fullName", is("Mark")));
  }

  @Test
  @DisplayName(
      "Given active mentees exist, when listing mentees, then return 200 OK with active only")
  void shouldListMenteesAndReturnOk() throws Exception {
    var mockMentee = createMenteeTest(2L, "Mark", "mark@test.com");
    when(menteeService.getAllMentees()).thenReturn(List.of(mockMentee));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(API_MENTEES)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].id", is(2)))
        .andExpect(jsonPath("$[0].fullName", is("Mark")));
  }

  @Test
  @DisplayName("Given pending mentees exist, when getting pending mentees, then return 200 OK")
  void shouldReturnPendingMenteesAndReturn200() throws Exception {
    var mockMentee = createMenteeTest(2L, "Mark", "mark@test.com");
    when(menteeAdminService.getPendingMentees()).thenReturn(List.of(mockMentee));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(API_MENTEES + "/pending")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].id", is(2)));
  }

  @Test
  @DisplayName("Given no pending mentees, when getting pending mentees, then return empty list")
  void shouldReturnEmptyListWhenNoPendingMentees() throws Exception {
    when(menteeAdminService.getPendingMentees()).thenReturn(List.of());

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(API_MENTEES + "/pending")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(0)));
  }

  @Test
  @DisplayName(
      "Given a pending mentee, when admin activates, then return 200 OK with active mentee")
  void shouldActivateMenteeAndReturn200() throws Exception {
    var activeMentee = createMenteeTest(10L, "Jane", "jane@test.com");
    when(menteeAdminService.activateMentee(10L)).thenReturn(activeMentee);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(API_MENTEES + "/10/activate")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(10)));
  }

  @Test
  @DisplayName("Given mentee not found, when admin activates, then return 404 NOT_FOUND")
  void shouldReturn404WhenActivatingNonExistentMentee() throws Exception {
    when(menteeAdminService.activateMentee(99L))
        .thenThrow(new ContentNotFoundException("Mentee not found: 99"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(API_MENTEES + "/99/activate")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName(
      "Given a pending mentee, when admin rejects with reason, then return 200 OK with rejected mentee")
  void shouldRejectMenteeAndReturn200() throws Exception {
    var rejectedMentee = createMenteeTest(10L, "Jane", "jane@test.com");
    when(menteeAdminService.rejectMentee(anyLong(), anyString())).thenReturn(rejectedMentee);

    final var request = new ApplicationRejectRequest(REJECTION_REASON);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(API_MENTEES + "/10/reject")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(10)));
  }

  @Test
  @DisplayName(
      "Given rejection reason is too short, when admin rejects, then return 400 BAD_REQUEST")
  void shouldReturn400WhenRejectionReasonTooShort() throws Exception {
    final var request = new ApplicationRejectRequest("Short");

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(API_MENTEES + "/10/reject")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Given mentee not found, when admin rejects, then return 404 NOT_FOUND")
  void shouldReturn404WhenRejectingNonExistentMentee() throws Exception {
    when(menteeAdminService.rejectMentee(anyLong(), anyString()))
        .thenThrow(new ContentNotFoundException("Mentee not found: 99"));

    final var request = new ApplicationRejectRequest(REJECTION_REASON);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(API_MENTEES + "/99/reject")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }
}
