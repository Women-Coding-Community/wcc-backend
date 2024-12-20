package com.wcc.platform.controller.platform;

import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.service.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for event pages API. */
@RestController
@RequestMapping("/api/platform/v1/page")
@Tag(name = "Platform", description = "All platform Internal APIs")
public class PageController {

  private final PageService service;

  @Autowired
  public PageController(final PageService service) {
    this.service = service;
  }

  /** Create Footer Page and store into database. */
  @PostMapping("/footer")
  @Operation(
      summary = "Create Footer Data",
      description = "Create footer data to be displayed in footer component.")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> createPage(@RequestBody final FooterPage footerPage) {
    return ResponseEntity.ok(service.create(footerPage));
  }

  /** Create Any Page and store into database. */
  @PostMapping()
  @Operation(
      summary = "Create any type of page",
      description = "Create new page with any content type.")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> createPage(@RequestBody final Object page) {
    return ResponseEntity.ok(service.create(page));
  }

  /** Create Landing Page and store into database. */
  @PostMapping("/landingPage")
  @Operation(
      summary = "Create landing page",
      description = "Create new page for landing page data.")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> createPage(@RequestBody final LandingPage page) {
    return ResponseEntity.ok(service.create(page));
  }

  /** Update Footer Page and store into database. */
  @PutMapping("/footer")
  @Operation(summary = "Update Footer Page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> updatePage(@RequestBody final FooterPage footerPage) {
    return ResponseEntity.ok(service.update(footerPage));
  }

  /** Update Landing Page and store into database. */
  @PutMapping("/landingPage")
  @Operation(
      summary = "Update landing page",
      description = "Update the content of existent landing page.")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> updatePage(@RequestBody final LandingPage page) {
    return ResponseEntity.ok(service.update(page));
  }

  /** Delete Page By ID. */
  @DeleteMapping
  @Operation(summary = "Delete page by id")
  public ResponseEntity<Void> deletePage(@RequestParam(name = "id") final String pageId) {
    service.deletePageById(pageId);
    return ResponseEntity.noContent().build();
  }
}
