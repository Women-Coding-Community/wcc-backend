package com.wcc.platform.controller.platform;

import com.wcc.platform.domain.cms.PageType;
import com.wcc.platform.service.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
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
@SecurityRequirement(name = "apiKey")
@Tag(name = "Platform: Pages", description = "Platform Internal Pages APIs")
@AllArgsConstructor
public class PageController {

  private final PageService service;

  /** Create Page Content and store into database. */
  @PostMapping
  @Operation(
      summary = "Create page content by page type",
      description = "Create new page with any content type.")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> createPage(
      @Parameter(description = "Page Type, for example: ABOUT_US", required = true)
          @RequestParam(name = "pageType")
          final PageType pageType,
      @RequestBody final Object page) {
    return ResponseEntity.ok(service.create(pageType, page));
  }

  /** Update Page and store into database. */
  @PutMapping
  @Operation(
      summary = "Update page content by page type",
      description = "Update the content of existent page.")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> updatePage(
      @Parameter(description = "Page Type, for example: ABOUT_US", required = true)
          @RequestParam(name = "pageType")
          final PageType pageType,
      @RequestBody final Object page) {
    return ResponseEntity.ok(service.update(pageType, page));
  }

  /** Delete Page By ID. */
  @DeleteMapping
  @Operation(summary = "Delete page by id")
  public ResponseEntity<Void> deletePage(@RequestParam(name = "id") final String pageId) {
    service.deletePageById(pageId);
    return ResponseEntity.noContent().build();
  }
}
