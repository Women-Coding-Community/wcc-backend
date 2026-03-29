package com.wcc.platform.controller;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.domain.exceptions.ApplicationNotFoundException;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.service.MenteeWorkflowService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/** Unit test for MenteeApplicationController. */
@ActiveProfiles("test")
@Import({SecurityConfig.class, TestConfig.class})
@WebMvcTest(MenteeApplicationController.class)
class MenteeApplicationControllerTest {

  private static final String APPROVE_URL =
      "/api/platform/v1/mentees/applications/{applicationId}/approve";
  private static final String REJECT_URL =
      "/api/platform/v1/mentees/applications/{applicationId}/reject";
  private static final String API_KEY_HEADER = "X-API-KEY";
  private static final String API_KEY_VALUE = "test-api-key";

  @Autowired private MockMvc mockMvc;
  @MockBean private MenteeWorkflowService applicationService;

  @Test
  @DisplayName(
      "Given a PENDING application, when admin approves it, then return 200 OK with updated application")
  void shouldApproveApplicationAndReturn200() throws Exception {
    final MenteeApplication approved =
        MenteeApplication.builder()
            .applicationId(1L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.MENTOR_REVIEWING)
            .whyMentor("Great mentor")
            .build();

    when(applicationService.approveApplication(1L)).thenReturn(approved);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(APPROVE_URL, 1L)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.applicationId").value(1))
        .andExpect(jsonPath("$.status").value("MENTOR_REVIEWING"));
  }

  @Test
  @DisplayName("Given a non-PENDING application, when admin approves it, then return 404 NOT_FOUND")
  void shouldReturn404WhenApprovedApplicationIsNotPending() throws Exception {
    when(applicationService.approveApplication(2L))
        .thenThrow(new ContentNotFoundException("No pending application with id 2"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(APPROVE_URL, 2L)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName(
      "Given an application that does not exist, when admin approves it, then return 404 NOT_FOUND")
  void shouldReturn404WhenApprovedApplicationDoesNotExist() throws Exception {
    when(applicationService.approveApplication(99L))
        .thenThrow(new ApplicationNotFoundException(99L));

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(APPROVE_URL, 99L)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName(
      "Given a PENDING application, when admin rejects it, then return 200 OK with updated application")
  void shouldRejectApplicationAndReturn200() throws Exception {
    final MenteeApplication rejected =
        MenteeApplication.builder()
            .applicationId(1L)
            .menteeId(10L)
            .mentorId(20L)
            .cycleId(5L)
            .priorityOrder(1)
            .status(ApplicationStatus.REJECTED)
            .whyMentor("Great mentor")
            .build();

    when(applicationService.rejectApplication(1L)).thenReturn(rejected);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(REJECT_URL, 1L)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.applicationId").value(1))
        .andExpect(jsonPath("$.status").value("REJECTED"));
  }

  @Test
  @DisplayName("Given a non-PENDING application, when admin rejects it, then return 404 NOT_FOUND")
  void shouldReturn404WhenRejectedApplicationIsNotPending() throws Exception {
    when(applicationService.rejectApplication(2L))
        .thenThrow(new ContentNotFoundException("No pending application with id 2"));

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(REJECT_URL, 2L)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName(
      "Given an application that does not exist, when admin rejects it, then return 404 NOT_FOUND")
  void shouldReturn404WhenRejectedApplicationDoesNotExist() throws Exception {
    when(applicationService.rejectApplication(99L))
        .thenThrow(new ApplicationNotFoundException(99L));

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(REJECT_URL, 99L)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
