package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.FooterPage;
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

/** Rest controller for footer api. */
@RestController
@RequestMapping("/api/cms/v1/")
@Tag(name = "API relevant to footer section")
public class FooterController {

  private final CmsService cmsService;

  @Autowired
  public FooterController(final CmsService cmsService) {
    this.cmsService = cmsService;
  }

  /**
   * API to retrieve footer section details.
   *
   * @return Footer content.
   */
  @GetMapping("/footer")
  @Operation(summary = "API to retrieve footer section details")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<FooterPage> getFooterPage() {
    return ResponseEntity.ok(cmsService.getFooter());
  }

  @GetMapping("/heath")
  @Operation(summary = "Heath Check api")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Boolean> isHeath() {
    return ResponseEntity.ok(true);
  }
}
