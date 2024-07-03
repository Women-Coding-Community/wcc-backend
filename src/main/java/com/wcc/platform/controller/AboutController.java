package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.service.CmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for about pages apis. */
@RestController
@RequestMapping("/api/cms/v1/")
@Tag(name = "APIs relevant About Us section")
public class AboutController {

  private final CmsService cmsService;

  @Autowired
  public AboutController(CmsService service) {
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
   * API to store information about members.
   *
   * @return Created new Member content.
   */
  @PutMapping("/member/")
  @Operation(summary = "API to submit member registration")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Member> createMember(@RequestBody Member member) {
    return ResponseEntity.ok(cmsService.createMember(member));
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
}
