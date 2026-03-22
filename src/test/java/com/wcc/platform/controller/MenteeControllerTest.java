package com.wcc.platform.controller;

import static com.wcc.platform.factories.SetupMenteeFactories.createMenteeTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.service.MenteeService;
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

  @Autowired private MockMvc mockMvc;
  @MockBean private MenteeService menteeService;

  @Test
  @DisplayName("Given valid mentee registration, when creating mentee, then return 201 Created")
  void shouldCreateMenteeAndReturnCreated() throws Exception {
    Mentee mockMentee = createMenteeTest(2L, "Mark", "mark@test.com");
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
  @DisplayName("Given mentees exist, when listing mentees, then return 200 OK")
  void shouldListMenteesAndReturnOk() throws Exception {
    Mentee mockMentee = createMenteeTest(2L, "Mark", "mark@test.com");
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
}
