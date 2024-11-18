package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.FiltersSection;
import com.wcc.platform.domain.cms.pages.events.EventsPage;
import com.wcc.platform.service.EventService;
import com.wcc.platform.service.FilterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for event pages API. */
@RestController
@RequestMapping("/api/cms/v1/")
@Tag(name = "APIs relevant Event Page")
public class EventController {

  private final EventService eventService;
  private final FilterService filterService;

  @Autowired
  public EventController(final EventService eventService, final FilterService filterService) {
    this.eventService = eventService;
    this.filterService = filterService;
  }

  /** API to retrieve information about events page. */
  @GetMapping("/events")
  @Operation(summary = "API to retrieve information about events page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<EventsPage> getEventsPage(
      @RequestParam(defaultValue = "1") final int currentPage,
      @RequestParam(defaultValue = "10") final int pageSize) {
    return ResponseEntity.ok(eventService.getEvents(currentPage, pageSize));
  }

  /** API to retrieve filters on events page. */
  @GetMapping("/events/filters")
  @Operation(summary = "API to retrieve information about filters on event page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<FiltersSection> getEventsFilters() {
    return ResponseEntity.ok(filterService.getEventsFilters());
  }
}
