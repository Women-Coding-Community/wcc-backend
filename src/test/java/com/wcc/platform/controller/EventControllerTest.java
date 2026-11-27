package com.wcc.platform.controller;

import static com.wcc.platform.factories.SetUpFiltersFactories.createFilterSectionTest;
import static com.wcc.platform.factories.SetupEventFactories.DEFAULT_CURRENT_PAGE;
import static com.wcc.platform.factories.SetupEventFactories.DEFAULT_PAGE_SIZE;
import static com.wcc.platform.factories.SetupEventFactories.createEventPageTest;
import static com.wcc.platform.factories.SetupEventFactories.createEventTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.factories.MockMvcRequestFactory;
import com.wcc.platform.service.EventService;
import com.wcc.platform.service.FilterService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Unit test for event controller. */
@ActiveProfiles("test")
@Import({SecurityConfig.class, TestConfig.class})
@WebMvcTest(EventController.class)
class EventControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private EventService eventService;

  @MockBean private FilterService filterService;

  @Test
  void testInternalServerError() throws Exception {
    when(eventService.getEvents(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE))
        .thenThrow(new PlatformInternalException("Invalid Json", new RuntimeException()));

    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest("/api/cms/v1/events").contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("Invalid Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/events")));
  }

  @Test
  void testOkResponseForEvents() throws Exception {
    var eventPage = createEventPageTest(List.of(createEventTest()));

    when(eventService.getEvents(DEFAULT_CURRENT_PAGE, DEFAULT_PAGE_SIZE)).thenReturn(eventPage);

    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest("/api/cms/v1/events").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(eventPage)));
  }

  @Test
  void testOkResponseForFilters() throws Exception {
    var eventsFilterSection = createFilterSectionTest();

    when(filterService.getEventsFilters()).thenReturn(eventsFilterSection);

    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest("/api/cms/v1/events/filters")
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(eventsFilterSection)));
  }

  @Test
  void testNotAcceptableForInvalidPageSize() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest("/api/cms/v1/events?currentPage=1&pageSize=0")
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotAcceptable())
        .andExpect(jsonPath("$.status", is(406)))
        .andExpect(
            jsonPath(
                "$.message", is("getEventsPage.pageSize: Page size must be greater than zero")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/events")));
  }

  @Test
  @DisplayName(
      "Given currentPage equals zero, when requesting events, "
          + "then it should be rejected as Not Acceptable")
  void shouldRejectCurrentPageLowerThanOne() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest("/api/cms/v1/events?currentPage=0&pageSize=10")
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotAcceptable())
        .andExpect(jsonPath("$.status", is(406)))
        .andExpect(
            jsonPath(
                "$.message",
                is("getEventsPage.currentPage: Current page must be greater than zero")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/events")));
  }

  @Test
  @DisplayName(
      "Given the requested page exceeds the total pages, when requesting events, "
          + "then it should return Bad Request instead of Internal Server Error")
  void shouldReturnBadRequestWhenCurrentPageExceedsTotalPages() throws Exception {
    var message = "currentPage exceeds total pages. Total Pages: 1, Current Page: 2";
    when(eventService.getEvents(DEFAULT_CURRENT_PAGE + 1, DEFAULT_PAGE_SIZE))
        .thenThrow(new IllegalArgumentException(message));

    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest("/api/cms/v1/events?currentPage=2&pageSize=10")
                .contentType(APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is(400)))
        .andExpect(jsonPath("$.message", is(message)))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/events")));
  }
}
