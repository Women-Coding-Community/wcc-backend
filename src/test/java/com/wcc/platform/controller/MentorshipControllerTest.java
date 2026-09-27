package com.wcc.platform.controller;

import static com.wcc.platform.factories.MockMvcRequestFactory.getRequest;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.service.MentorshipService;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Unit test for MentorshipController. */
@ActiveProfiles("test")
@Import({SecurityConfig.class, TestConfig.class})
@WebMvcTest(MentorshipController.class)
class MentorshipControllerTest {

  private static final String API_CURRENT_CYCLE = "/api/platform/v1/mentorship/cycles/current";

  @Autowired private MockMvc mockMvc;
  @MockBean private MentorshipService mentorshipService;

  @Test
  @DisplayName(
      "Given a cycle is open for registration, when getting the current cycle, then return 200 OK with the cycle")
  void shouldReturnOpenCycle() throws Exception {
    final LocalDate today = LocalDate.now();
    when(mentorshipService.getCurrentCycle())
        .thenReturn(
            MentorshipCycleEntity.builder()
                .cycleId(6L)
                .registrationStartDate(today.minusDays(5))
                .registrationEndDate(today.plusDays(10))
                .status(CycleStatus.OPEN)
                .build());

    mockMvc
        .perform(getRequest(API_CURRENT_CYCLE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cycleId", is(6)))
        .andExpect(jsonPath("$.status", is("OPEN")))
        .andExpect(jsonPath("$.registrationOpen", is(true)));
  }

  @Test
  @DisplayName(
      "Given no cycle is open for registration, when getting the current cycle, then return 200 OK with status CLOSED")
  void shouldReturnClosedCycleWhenNoneIsOpen() throws Exception {
    when(mentorshipService.getCurrentCycle()).thenReturn(MentorshipService.CLOSED_CYCLE);

    mockMvc
        .perform(getRequest(API_CURRENT_CYCLE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("CLOSED")))
        .andExpect(jsonPath("$.registrationOpen", is(false)));
  }
}
