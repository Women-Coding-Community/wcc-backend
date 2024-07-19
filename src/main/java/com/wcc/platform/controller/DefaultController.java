package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.FooterPage;
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
@Tag(name = "General APIs")
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
  public ResponseEntity<FooterPage> getFooterPage() {
    return ResponseEntity.ok(cmsService.getFooter());
  }

  /**
   * Create default api response for WCC Platform
   *
   * @return "Women Coding Community Platform"
   */
  @GetMapping("/")
  @Operation(summary = "API to retrieve default page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> helloWorld() {
    return ResponseEntity.ok("Women Coding Community Platform");
  }
}
