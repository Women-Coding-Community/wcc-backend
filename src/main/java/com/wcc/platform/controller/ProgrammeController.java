package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.programme.ProgrammePage;
import com.wcc.platform.service.ProgrammeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for all the programme apis. */
@RestController
@RequestMapping("/api/cms/v1/programme")
@Tag(name = "APIs relevant to Programme pages")
public class ProgrammeController {

  private final ProgrammeService service;

  @Autowired
  public ProgrammeController(final ProgrammeService programmeService) {
    this.service = programmeService;
  }

  @GetMapping("/bookClub")
  @Operation(summary = "API to retrieve book club programme page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<ProgrammePage> getBookClubPage() {
    return ResponseEntity.ok(service.getBookClub());
  }
}
