package com.wcc.platform.service;

import static com.wcc.platform.factories.SetupEventFactories.createEventPageTest;
import static com.wcc.platform.factories.SetupEventFactories.createEventTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.EventsPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class EventServiceTest {

  private ObjectMapper objectMapper;
  private EventService service;

  @BeforeEach
  void setUp() {
    objectMapper = Mockito.mock(ObjectMapper.class);
    service = new EventService(objectMapper);
  }

  @Test
  void whenGetEventsValidJson() throws IOException {
    var page = createEventPageTest(List.of(createEventTest()));
    when(objectMapper.readValue(anyString(), eq(EventsPage.class))).thenReturn(page);

    var response = service.getEvents();

    assertEquals(page, response);
  }

  @Test
  void whenGetEventsInValidJson() throws IOException {
    when(objectMapper.readValue(anyString(), eq(EventsPage.class)))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});

    var exception = assertThrows(PlatformInternalException.class, service::getEvents);

    assertEquals("Invalid JSON", exception.getMessage());
  }
}
