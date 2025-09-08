package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.programme.ProgrammePage;
import com.wcc.platform.domain.platform.type.ProgramType;
import com.wcc.platform.service.ProgrammeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for all the programme apis. */
@RestController
@SecurityRequirement(name = "apiKey")
public class ProgrammeController {

  private final ProgrammeService service;

  @Autowired
  public ProgrammeController(final ProgrammeService programmeService) {
    this.service = programmeService;
  }

  /** Get program API. */
  @Tag(name = "Pages and Sections", description = "Pages and/or sections APIs")
  @GetMapping("/api/cms/v1/program")
  @Operation(
      summary = "API to retrieve programme page",
      description = "Select each programme by type")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<ProgrammePage> getProgramme(
      @Parameter(description = "Program Type, for example: BOOK_CLUB", required = true)
          @RequestParam(name = "type")
          final ProgramType programType) {

    return ResponseEntity.ok(service.getProgramme(programType));
  }

  /** Create Programme Page Content and store into database. */
  @Tag(name = "Platform: Program", description = "Program Internal APIs")
  @PostMapping("/api/platform/v1/program")
  @Operation(
      summary = "Create program page content by program type",
      description = "Create program new page with any content type.")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> createPage(
      @Parameter(description = "Program Type, for example: BOOK_CLUB", required = true)
          @RequestParam(name = "type")
          final ProgramType type,
      @RequestBody final Object page) {
    return ResponseEntity.ok(service.create(type, page));
  }

  /** Update Program Page and store into database. */
  @Tag(name = "Platform: Program", description = "Program Internal APIs")
  @PutMapping("/api/platform/v1/program")
  @Operation(
      summary = "Update program page content by program type",
      description = "Update the content of existent program page.")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Object> updatePage(
      @Parameter(description = "Program Type, for example: BOOK_CLUB", required = true)
          @RequestParam(name = "type")
          final ProgramType type,
      @RequestBody final Object page) {
    return ResponseEntity.ok(service.update(type, page));
  }
}
