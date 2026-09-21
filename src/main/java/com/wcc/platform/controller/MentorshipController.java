package com.wcc.platform.controller;

import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.service.MentorshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for public mentorship apis. */
@RestController
@RequestMapping("/api/platform/v1/mentorship")
@SecurityRequirement(name = "apiKey")
@Tag(name = "Platform: Mentorship", description = "Public APIs for mentorship")
@AllArgsConstructor
public class MentorshipController {

  private final MentorshipService mentorshipService;

  /**
   * API to get the mentorship cycle that is currently open for registration.
   *
   * @return Current open cycle, or a cycle with status CLOSED if none is open
   */
  @GetMapping("/cycles/current")
  @Operation(summary = "API to retrieve the mentorship cycle currently open for registration")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipCycleEntity> getCurrentCycle() {
    return ResponseEntity.ok(mentorshipService.getCurrentCycle());
  }
}
