package com.wcc.platform.controller;

import static com.wcc.platform.factories.MockMvcRequestFactory.getRequest;
import static com.wcc.platform.factories.MockMvcRequestFactory.postRequest;
import static com.wcc.platform.factories.SetupFeedbackFactories.createCommunityGeneralFeedbackDtoTest;
import static com.wcc.platform.factories.SetupFeedbackFactories.createCommunityGeneralFeedbackTest;
import static com.wcc.platform.factories.SetupFeedbackFactories.createMentorReviewFeedbackDtoTest;
import static com.wcc.platform.factories.SetupFeedbackFactories.createMentorReviewFeedbackTest;
import static com.wcc.platform.factories.SetupFeedbackFactories.createMentorshipProgramFeedbackTest;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.domain.exceptions.FeedbackNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.feedback.Feedback;
import com.wcc.platform.domain.platform.feedback.FeedbackDto;
import com.wcc.platform.domain.platform.feedback.FeedbackSearchCriteria;
import com.wcc.platform.domain.platform.type.FeedbackType;
import com.wcc.platform.service.FeedbackService;
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

/** Unit test for Feedback APIs. */
@ActiveProfiles("test")
@Import({SecurityConfig.class, TestConfig.class})
@WebMvcTest(FeedbackController.class)
class FeedbackControllerTest {

  private static final String API_FEEDBACK = "/api/platform/v1/feedback";
  private static final String API_KEY_HEADER = "X-API-KEY";
  private static final String API_KEY_VALUE = "test-api-key";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired private MockMvc mockMvc;
  @MockBean private FeedbackService feedbackService;

  @Test
  @DisplayName(
      "Given valid feedback ID, when getting feedback by ID, then returns OK with feedback")
  void testGetFeedbackByIdReturnsOk() throws Exception {
    Long feedbackId = 1L;
    Feedback mockFeedback = createMentorReviewFeedbackTest();
    when(feedbackService.getFeedbackById(feedbackId)).thenReturn(mockFeedback);

    mockMvc
        .perform(getRequest(API_FEEDBACK + "/" + feedbackId).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.reviewerId", is(1)))
        .andExpect(jsonPath("$.reviewerName", is("Mentee Reviewer")))
        .andExpect(jsonPath("$.feedbackType", is("MENTOR_REVIEW")))
        .andExpect(jsonPath("$.rating", is(5)));
  }

  @Test
  @DisplayName(
      "Given non-existent feedback ID, when getting feedback by ID, then returns not found")
  void testGetFeedbackByIdNotFound() throws Exception {
    Long feedbackId = 999L;
    when(feedbackService.getFeedbackById(feedbackId))
        .thenThrow(new FeedbackNotFoundException(feedbackId));

    mockMvc
        .perform(getRequest(API_FEEDBACK + "/" + feedbackId).contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName(
      "Given valid mentor review feedback DTO, when creating feedback, then returns created with feedback")
  void testCreateFeedbackReturnsCreated() throws Exception {
    FeedbackDto feedbackDto = createMentorReviewFeedbackDtoTest();
    Feedback mockFeedback = createMentorReviewFeedbackTest();
    when(feedbackService.createFeedback(any(FeedbackDto.class))).thenReturn(mockFeedback);

    mockMvc
        .perform(postRequest(API_FEEDBACK, feedbackDto))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.reviewerId", is(1)))
        .andExpect(jsonPath("$.revieweeId", is(2)))
        .andExpect(jsonPath("$.feedbackType", is("MENTOR_REVIEW")))
        .andExpect(jsonPath("$.rating", is(5)))
        .andExpect(jsonPath("$.feedbackText", is("This is a test feedback")));
  }

  @Test
  @DisplayName(
      "Given valid community general feedback DTO, when creating feedback, then returns created with feedback")
  void testCreateCommunityFeedbackReturnsCreated() throws Exception {
    FeedbackDto feedbackDto = createCommunityGeneralFeedbackDtoTest();
    Feedback mockFeedback = createCommunityGeneralFeedbackTest();
    when(feedbackService.createFeedback(any(FeedbackDto.class))).thenReturn(mockFeedback);

    mockMvc
        .perform(postRequest(API_FEEDBACK, feedbackDto))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(2)))
        .andExpect(jsonPath("$.feedbackType", is("COMMUNITY_GENERAL")))
        .andExpect(jsonPath("$.rating", is(4)))
        .andExpect(jsonPath("$.feedbackText", is("Great community experience")));
  }

  @Test
  @DisplayName(
      "Given valid feedback ID and update DTO, when updating feedback, then returns OK with updated feedback")
  void testUpdateFeedbackReturnsOk() throws Exception {
    Long feedbackId = 1L;
    FeedbackDto feedbackDto = createMentorReviewFeedbackDtoTest();
    Feedback updatedFeedback =
        createMentorReviewFeedbackTest().toBuilder()
            .feedbackText("Updated feedback text")
            .rating(4)
            .build();

    when(feedbackService.updateFeedback(eq(feedbackId), any(FeedbackDto.class)))
        .thenReturn(updatedFeedback);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(API_FEEDBACK + "/" + feedbackId)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.feedbackText", is("Updated feedback text")))
        .andExpect(jsonPath("$.rating", is(4)));
  }

  @Test
  @DisplayName("Given non-existent feedback ID, when updating feedback, then returns not found")
  void testUpdateNonExistentFeedbackThrowsException() throws Exception {
    Long nonExistentFeedbackId = 999L;
    FeedbackDto feedbackDto = createMentorReviewFeedbackDtoTest();

    when(feedbackService.updateFeedback(eq(nonExistentFeedbackId), any(FeedbackDto.class)))
        .thenThrow(new FeedbackNotFoundException(nonExistentFeedbackId));

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(API_FEEDBACK + "/" + nonExistentFeedbackId)
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Given valid feedback ID, when approving feedback, then returns OK")
  void testApproveFeedbackReturnsOk() throws Exception {
    Long feedbackId = 1L;
    doNothing().when(feedbackService).approveFeedback(feedbackId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(API_FEEDBACK + "/" + feedbackId + "/approve")
                .header(API_KEY_HEADER, API_KEY_VALUE))
        .andExpect(status().isOk());

    verify(feedbackService).approveFeedback(feedbackId);
  }

  @Test
  @DisplayName("Given non-existent feedback ID, when approving feedback, then returns not found")
  void testApproveNonExistentFeedbackThrowsException() throws Exception {
    Long nonExistentFeedbackId = 999L;
    doThrow(new FeedbackNotFoundException(nonExistentFeedbackId))
        .when(feedbackService)
        .approveFeedback(nonExistentFeedbackId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(API_FEEDBACK + "/" + nonExistentFeedbackId + "/approve")
                .header(API_KEY_HEADER, API_KEY_VALUE))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName(
      "Given valid feedback ID and anonymous status true, when updating anonymous status, then returns OK")
  void testUpdateFeedbackAnonymousStatusReturnsOk() throws Exception {
    Long feedbackId = 1L;
    Boolean isAnonymous = true;
    doNothing().when(feedbackService).updateFeedbackAnonymousStatus(feedbackId, isAnonymous);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(API_FEEDBACK + "/" + feedbackId + "/anonymous-status")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .param("isAnonymous", isAnonymous.toString()))
        .andExpect(status().isOk());

    verify(feedbackService).updateFeedbackAnonymousStatus(feedbackId, isAnonymous);
  }

  @Test
  @DisplayName(
      "Given valid feedback ID and anonymous status false, when updating anonymous status, then returns OK")
  void testUpdateFeedbackAnonymousStatusToFalseReturnsOk() throws Exception {
    Long feedbackId = 1L;
    Boolean isAnonymous = false;
    doNothing().when(feedbackService).updateFeedbackAnonymousStatus(feedbackId, isAnonymous);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(API_FEEDBACK + "/" + feedbackId + "/anonymous-status")
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .param("isAnonymous", isAnonymous.toString()))
        .andExpect(status().isOk());

    verify(feedbackService).updateFeedbackAnonymousStatus(feedbackId, isAnonymous);
  }

  @Test
  @DisplayName("Given valid feedback ID, when deleting feedback, then returns no content")
  void testDeleteFeedbackReturnsNoContent() throws Exception {
    Long feedbackId = 1L;
    doNothing().when(feedbackService).deleteFeedback(feedbackId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(API_FEEDBACK + "/" + feedbackId)
                .header(API_KEY_HEADER, API_KEY_VALUE))
        .andExpect(status().isNoContent());

    verify(feedbackService).deleteFeedback(feedbackId);
  }

  @Test
  @DisplayName("Given non-existent feedback ID, when deleting feedback, then returns not found")
  void testDeleteNonExistentFeedbackThrowsException() throws Exception {
    Long nonExistentFeedbackId = 999L;
    doThrow(new FeedbackNotFoundException(nonExistentFeedbackId))
        .when(feedbackService)
        .deleteFeedback(nonExistentFeedbackId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(API_FEEDBACK + "/" + nonExistentFeedbackId)
                .header(API_KEY_HEADER, API_KEY_VALUE))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Given no query filters, when getting all feedback, then returns all feedback")
  void testGetAllFeedbackNoFilters() throws Exception {
    Feedback feedback1 = createMentorReviewFeedbackTest();
    Feedback feedback2 = createCommunityGeneralFeedbackTest();
    Feedback feedback3 = createMentorshipProgramFeedbackTest();
    List<Feedback> mockFeedbackList = List.of(feedback1, feedback2, feedback3);

    when(feedbackService.getAllFeedback(any(FeedbackSearchCriteria.class)))
        .thenReturn(mockFeedbackList);

    mockMvc
        .perform(getRequest(API_FEEDBACK).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].feedbackType", is("MENTOR_REVIEW")))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].feedbackType", is("COMMUNITY_GENERAL")))
        .andExpect(jsonPath("$[2].id", is(3)))
        .andExpect(jsonPath("$[2].feedbackType", is("MENTORSHIP_PROGRAM")));

    verify(feedbackService)
        .getAllFeedback(
            argThat(
                criteria ->
                    criteria.getReviewerId() == null
                        && criteria.getRevieweeId() == null
                        && criteria.getFeedbackType() == null
                        && criteria.getYear() == null));
  }

  @Test
  @DisplayName(
      "Given reviewer ID filter, when getting all feedback, then returns feedback for reviewer")
  void testGetAllFeedbackWithReviewerId() throws Exception {
    Long reviewerId = 1L;
    Feedback feedback1 = createMentorReviewFeedbackTest();
    Feedback feedback2 = createMentorReviewFeedbackTest();
    feedback2.setId(4L);
    List<Feedback> mockFeedbackList = List.of(feedback1, feedback2);

    when(feedbackService.getAllFeedback(any(FeedbackSearchCriteria.class)))
        .thenReturn(mockFeedbackList);

    mockMvc
        .perform(
            getRequest(API_FEEDBACK)
                .param("reviewerId", reviewerId.toString())
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].reviewerId", is(1)))
        .andExpect(jsonPath("$[1].reviewerId", is(1)));

    verify(feedbackService)
        .getAllFeedback(
            argThat(
                criteria ->
                    criteria.getReviewerId().equals(reviewerId)
                        && criteria.getRevieweeId() == null
                        && criteria.getFeedbackType() == null));
  }

  @Test
  @DisplayName(
      "Given reviewee ID filter, when getting all feedback, then returns feedback for reviewee")
  void testGetAllFeedbackWithRevieweeId() throws Exception {
    Long revieweeId = 2L;
    Feedback feedback1 = createMentorReviewFeedbackTest();
    List<Feedback> mockFeedbackList = List.of(feedback1);

    when(feedbackService.getAllFeedback(any(FeedbackSearchCriteria.class)))
        .thenReturn(mockFeedbackList);

    mockMvc
        .perform(
            getRequest(API_FEEDBACK)
                .param("revieweeId", revieweeId.toString())
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].revieweeId", is(2)));

    verify(feedbackService)
        .getAllFeedback(
            argThat(
                criteria ->
                    criteria.getRevieweeId().equals(revieweeId)
                        && criteria.getReviewerId() == null
                        && criteria.getFeedbackType() == null));
  }

  @Test
  @DisplayName(
      "Given feedback type filter, when getting all feedback, then returns feedback of specified type")
  void testGetAllFeedbackWithType() throws Exception {
    Feedback feedback1 = createMentorReviewFeedbackTest();
    Feedback feedback2 = createMentorReviewFeedbackTest();
    feedback2.setId(5L);
    List<Feedback> mockFeedbackList = List.of(feedback1, feedback2);

    when(feedbackService.getAllFeedback(any(FeedbackSearchCriteria.class)))
        .thenReturn(mockFeedbackList);

    mockMvc
        .perform(
            getRequest(API_FEEDBACK)
                .param("feedbackType", "MENTOR_REVIEW")
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].feedbackType", is("MENTOR_REVIEW")))
        .andExpect(jsonPath("$[1].feedbackType", is("MENTOR_REVIEW")));

    verify(feedbackService)
        .getAllFeedback(
            argThat(
                criteria ->
                    criteria.getFeedbackType() == FeedbackType.MENTOR_REVIEW
                        && criteria.getReviewerId() == null
                        && criteria.getRevieweeId() == null));
  }

  @Test
  @DisplayName(
      "Given year filter, when getting all feedback, then returns feedback for specified year")
  void testGetAllFeedbackWithYear() throws Exception {
    Integer year = 2026;
    Feedback feedback1 = createMentorReviewFeedbackTest();
    Feedback feedback2 = createCommunityGeneralFeedbackTest();
    List<Feedback> mockFeedbackList = List.of(feedback1, feedback2);

    when(feedbackService.getAllFeedback(any(FeedbackSearchCriteria.class)))
        .thenReturn(mockFeedbackList);

    mockMvc
        .perform(
            getRequest(API_FEEDBACK).param("year", year.toString()).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].year", is(2026)))
        .andExpect(jsonPath("$[1].year", is(2026)));

    verify(feedbackService)
        .getAllFeedback(
            argThat(
                criteria ->
                    criteria.getYear().equals(year)
                        && criteria.getReviewerId() == null
                        && criteria.getRevieweeId() == null
                        && criteria.getFeedbackType() == null));
  }

  @Test
  @DisplayName(
      "Given multiple filters, when getting all feedback, then returns feedback matching all filters")
  void testGetAllFeedbackMultipleFilters() throws Exception {
    Long reviewerId = 1L;
    Integer year = 2026;
    Feedback feedback1 = createMentorReviewFeedbackTest();
    List<Feedback> mockFeedbackList = List.of(feedback1);

    when(feedbackService.getAllFeedback(any(FeedbackSearchCriteria.class)))
        .thenReturn(mockFeedbackList);

    mockMvc
        .perform(
            getRequest(API_FEEDBACK)
                .param("reviewerId", reviewerId.toString())
                .param("feedbackType", "MENTOR_REVIEW")
                .param("year", year.toString())
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].reviewerId", is(1)))
        .andExpect(jsonPath("$[0].feedbackType", is("MENTOR_REVIEW")))
        .andExpect(jsonPath("$[0].year", is(2026)));

    verify(feedbackService)
        .getAllFeedback(
            argThat(
                criteria ->
                    criteria.getReviewerId().equals(reviewerId)
                        && criteria.getFeedbackType() == FeedbackType.MENTOR_REVIEW
                        && criteria.getYear().equals(year)));
  }

  @Test
  @DisplayName(
      "Given service throws exception, when getting all feedback, then returns internal server error")
  void testInternalServerError() throws Exception {
    when(feedbackService.getAllFeedback(any(FeedbackSearchCriteria.class)))
        .thenThrow(new PlatformInternalException("Invalid Json", new RuntimeException()));

    mockMvc
        .perform(getRequest(API_FEEDBACK).contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("Invalid Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/platform/v1/feedback")));
  }
}
