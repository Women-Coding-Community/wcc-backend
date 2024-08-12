package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.EventsPage;
import com.wcc.platform.service.CmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for event pages API. */
@RestController
@RequestMapping("/api/cms/v1/")
@Tag(name = "APIs relevant Event Page")
public class EventController {

  private final CmsService cmsService;

  @Autowired
  public EventController(final CmsService service) {
    this.cmsService = service;
  }

  /** API to retrieve information about events page. */
  @GetMapping("/events")
  @Operation(summary = "API to retrieve information about events page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<EventsPage> getEventsPage() {
    return ResponseEntity.ok(cmsService.getEvents());
  }
}
