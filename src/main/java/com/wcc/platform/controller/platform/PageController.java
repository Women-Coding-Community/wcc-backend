package com.wcc.platform.controller.platform;

import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.service.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  @Operation(summary = "Create Footer Page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> createPage(@RequestBody final FooterPage footerPage) {
    return ResponseEntity.ok(service.create(footerPage));
  }

  /** Create Landing Page and store into database. */
  @PostMapping("/landingPage")
  @Operation(summary = "Update landing page", description = "Update landing page data.")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> createPage(@RequestBody final LandingPage page) {
    return ResponseEntity.ok(service.create(page));
  }

  /** Create Footer Page and store into database. */
  @PutMapping("/footer")
  @Operation(summary = "Save Footer Page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> updatePage(@RequestBody final FooterPage footerPage) {
    return ResponseEntity.ok(service.update(footerPage));
  }

  /** Update Landing Page and store into database. */
  @PutMapping("/landingPage")
  @Operation(
      summary = "Update landing page",
      description = "Update a page which represents landing page.")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> updatePage(@RequestBody final LandingPage page) {
    return ResponseEntity.ok(service.update(page));
  }

  /** Get all pages. */
  @GetMapping("/all")
  @Operation(summary = "Get All Pages")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> getPage() {
    return ResponseEntity.ok(service.findAll());
  }
}
