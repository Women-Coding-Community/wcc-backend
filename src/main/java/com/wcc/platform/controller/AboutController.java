package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.AboutUs;
import com.wcc.platform.domain.cms.pages.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.TeamPage;
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

/** Rest controller for about pages apis. */
@RestController
@RequestMapping("/api/cms/v1/")
@Tag(name = "Pages: About Us", description = "All APIs under session About-Us")
public class AboutController {

  private final CmsService cmsService;

  @Autowired
  public AboutController(final CmsService service) {
    this.cmsService = service;
  }

  /**
   * API to retrieve information about leadership team members.
   *
   * @return Leadership team page content.
   */
  @GetMapping("/team")
  @Operation(summary = "API to retrieve information about leadership team members")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<TeamPage> getTeamPage() {
    return ResponseEntity.ok(cmsService.getTeam());
  }

  /**
   * API to retrieve information about collaborators.
   *
   * @return Collaborators page content.
   */
  @GetMapping("/collaborators")
  @Operation(summary = "API to retrieve information about collaborators")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<CollaboratorPage> getCollaboratorPage() {
    return ResponseEntity.ok(cmsService.getCollaborator());
  }

  /**
   * API to retrieve information about Code of conduct.
   *
   * @return Code of conduct page content.
   */
  @GetMapping("/code-of-conduct")
  @Operation(summary = "API to retrieve information about Code of conduct")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<CodeOfConductPage> getCodeOfConductPage() {
    return ResponseEntity.ok(cmsService.getCodeOfConduct());
  }

  /**
   * API to retrieve information about "About Us" page.
   *
   * @return AboutUs page
   */
  @GetMapping("/about")
  @Operation(summary = "API to retrieve information about About Us page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<AboutUs> getAboutUsPage() {
    return ResponseEntity.ok(cmsService.getAboutUs());
  }
}
