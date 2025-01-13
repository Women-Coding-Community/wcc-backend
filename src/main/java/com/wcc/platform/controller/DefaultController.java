package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.FooterSection;
import com.wcc.platform.domain.cms.pages.LandingPage;
import com.wcc.platform.service.CmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for footer api. */
@RestController
@Tag(name = "Pages: Others", description = "All other APIs")
public class DefaultController {

  private final CmsService cmsService;

  @Autowired
  public DefaultController(final CmsService cmsService) {
    this.cmsService = cmsService;
  }

  /**
   * API to retrieve footer section details.
   *
   * @return Footer content.
   */
  @GetMapping("/api/cms/v1/footer")
  @Operation(summary = "API to retrieve footer section details")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<FooterSection> getFooterPage() {
    return ResponseEntity.ok(cmsService.getFooter());
  }

  /** API to retrieve landing page. */
  @GetMapping("/api/cms/v1/landingPage")
  @Operation(summary = "API to retrieve landing page sections")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<LandingPage> getLandingPage() {
    return ResponseEntity.ok(cmsService.getLandingPage());
  }
}
