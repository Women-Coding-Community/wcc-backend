package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipCodeOfConductPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipFaqPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipStudyGroupsPage;
import com.wcc.platform.service.MentorshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for mentorship apis. */
@RestController
@RequestMapping("/api/cms/v1/mentorship")
@SecurityRequirement(name = "apiKey")
@Tag(name = "Pages: Mentorship", description = "All APIs under session Mentorship")
public class MentorshipController {

  private final MentorshipService service;

  @Autowired
  public MentorshipController(final MentorshipService service) {
    this.service = service;
  }

  @GetMapping("/overview")
  @Operation(summary = "API to retrieve mentorship overview page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipPage> getMentorshipOverview() {
    return ResponseEntity.ok(service.getOverview());
  }

  @GetMapping("/faq")
  @Operation(summary = "API to retrieve mentorship faq page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipFaqPage> getMentorshipFaq() {
    return ResponseEntity.ok(service.getFaq());
  }

  @GetMapping("/code-of-conduct")
  @Operation(summary = "API to retrieve mentorship code of conduct page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipCodeOfConductPage> getMentorshipCodeOfConduct() {
    return ResponseEntity.ok(service.getCodeOfConduct());
  }

  @GetMapping("/study-groups")
  @Operation(summary = "API to retrieve mentorship study groups page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipStudyGroupsPage> getMentorshipStudyGroup() {
    return ResponseEntity.ok(service.getStudyGroups());
  }

  @GetMapping("/mentors")
  @Operation(summary = "API to retrieve mentors page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorsPage> getMentors() {
    return ResponseEntity.ok(service.getMentors());
  }
}
