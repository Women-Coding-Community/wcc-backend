package com.wcc.platform.controller;

import static com.wcc.platform.factories.SetupEventFactories.createEventPageTest;
import static com.wcc.platform.factories.SetupEventFactories.createEventTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.service.CmsService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/** Unit test for event controller. */
@WebMvcTest(EventController.class)
public class EventControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private CmsService service;

  @Test
  void testInternalServerError() throws Exception {
    when(service.getEvents())
        .thenThrow(new PlatformInternalException("Invalid Json", new RuntimeException()));

    mockMvc
        .perform(get("/api/cms/v1/events").contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("Invalid Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/events")));
  }

  @Test
  void testOkResponse() throws Exception {
    var eventPage = createEventPageTest(List.of(createEventTest()));

    when(service.getEvents()).thenReturn(eventPage);

    mockMvc
        .perform(get("/api/cms/v1/events").contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(eventPage)));
  }
}
