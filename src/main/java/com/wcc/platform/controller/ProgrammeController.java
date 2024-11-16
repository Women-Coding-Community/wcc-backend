package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.programme.ProgrammePage;
import com.wcc.platform.domain.platform.ProgramType;
import com.wcc.platform.service.ProgrammeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for all the programme apis. */
@RestController
@Tag(name = "Pages: Programme", description = "All APIs under session Programme")
public class ProgrammeController {

  private final ProgrammeService service;

  @Autowired
  public ProgrammeController(final ProgrammeService programmeService) {
    this.service = programmeService;
  }

  /** Get programm API. For now only supports Book Club. */
  @GetMapping("/api/cms/v1/programme")
  @Operation(summary = "API to retrieve programme page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<ProgrammePage> getProgramme(
      @RequestParam(name = "type") final String programmeType) {
    final ProgramType programType = ProgramType.findByValue(programmeType);

    if (ProgramType.BOOK_CLUB.equals(programType)) {
      return ResponseEntity.ok(service.getProgramme(programType));
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }
}
