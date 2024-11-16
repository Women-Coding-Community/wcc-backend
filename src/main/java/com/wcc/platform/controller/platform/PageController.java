package com.wcc.platform.controller.platform;

import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.service.PlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for event pages API. */
@RestController
@RequestMapping("/api/platform/v1/page")
@Tag(name = "Platform Page APIs")
public class PageController {

  private final PlatformService service;

  @Autowired
  public PageController(final PlatformService service) {
    this.service = service;
  }

  /** Create Footer Page and store into database. */
  @PostMapping("/footer")
  @Operation(summary = "Save Footer Page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> savePage(@RequestBody final FooterPage footerPage) {
    return ResponseEntity.ok(service.savePage(footerPage));
  }

  /** Create Landing Page and store into database. */
  @PostMapping("/landingPage")
  @Operation(summary = "Save Landing Page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> saveLandingPage(@RequestBody final LandingPage page) {
    return ResponseEntity.ok(service.savePage(page));
  }

  /** Delete Page By ID. */
  @DeleteMapping
  @Operation(summary = "Delete page by id")
  public ResponseEntity<Void> deletePage(@RequestParam(name = "id") final String pageId) {
    service.deletePageById(pageId);
    return ResponseEntity.noContent().build();
  }

  /** Get all pages content. */
  @GetMapping
  @Operation(summary = "Get all pages")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Collection<String>> getAllResources() {
    return ResponseEntity.ok(service.getAllPages());
  }
}
