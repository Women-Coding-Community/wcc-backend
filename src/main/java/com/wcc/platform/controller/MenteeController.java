package com.wcc.platform.controller;

import com.wcc.platform.configuration.security.RequiresPermission;
import com.wcc.platform.domain.auth.Permission;
import com.wcc.platform.domain.platform.mentorship.ApplicationRejectRequest;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeRegistration;
import com.wcc.platform.service.MenteeAdminService;
import com.wcc.platform.service.MenteeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for mentees page apis. */
@RestController
@RequestMapping("/api/platform/v1")
@SecurityRequirement(name = "apiKey")
@Tag(
    name = "Platform: Mentees",
    description = "APIs for mentee registration and retrieval independent of mentorship cycles")
@AllArgsConstructor
@Validated
public class MenteeController {

  private final MenteeService menteeService;
  private final MenteeAdminService menteeAdminService;

  /**
   * API to create mentee.
   *
   * @param menteeRegistration The mentee registration details
   * @return Create a new mentee.
   */
  @PostMapping("/mentees")
  @Operation(summary = "API to submit mentee registration")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Mentee> createMentee(
      @Valid @RequestBody final MenteeRegistration menteeRegistration) {
    return new ResponseEntity<>(
        menteeService.saveRegistration(menteeRegistration), HttpStatus.CREATED);
  }

  /**
   * Retrieves a list of all active mentees (status_id = 1).
   *
   * @return a list of active mentees
   */
  @GetMapping("/mentees")
  @Operation(summary = "API to list all active mentees")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<Mentee>> listMentees() {
    return new ResponseEntity<>(menteeService.getAllMentees(), HttpStatus.OK);
  }

  /**
   * Retrieves all mentees with PENDING status awaiting admin review.
   *
   * @return a list of pending mentees
   */
  @GetMapping("/mentees/pending")
  @RequiresPermission(Permission.MENTEE_APPROVE)
  @Operation(
      summary = "Get all pending mentees awaiting admin activation",
      security = {@SecurityRequirement(name = "apiKey"), @SecurityRequirement(name = "bearerAuth")})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<Mentee>> getPendingMentees() {
    return ResponseEntity.ok(menteeAdminService.getPendingMentees());
  }

  /**
   * Admin activates a mentee by setting their profile status to ACTIVE.
   *
   * @param menteeId The mentee ID
   * @return the activated mentee
   */
  @PatchMapping("/mentees/{menteeId}/activate")
  @RequiresPermission(Permission.MENTEE_APPROVE)
  @Operation(
      summary = "Admin activates a mentee (sets status to ACTIVE)",
      security = {@SecurityRequirement(name = "apiKey"), @SecurityRequirement(name = "bearerAuth")})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Mentee> activateMentee(
      @Parameter(description = "Mentee ID") @PathVariable final Long menteeId) {
    return ResponseEntity.ok(menteeAdminService.activateMentee(menteeId));
  }

  /**
   * Admin rejects a mentee by setting their profile status to REJECTED and rejecting all pending
   * applications.
   *
   * @param menteeId The mentee ID
   * @param request Rejection request containing the reason
   * @return the rejected mentee
   */
  @PatchMapping("/mentees/{menteeId}/reject")
  @RequiresPermission(Permission.MENTEE_APPROVE)
  @Operation(
      summary = "Admin rejects a mentee (sets status to REJECTED and rejects all applications)",
      security = {@SecurityRequirement(name = "apiKey"), @SecurityRequirement(name = "bearerAuth")})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Mentee> rejectMentee(
      @Parameter(description = "Mentee ID") @PathVariable final Long menteeId,
      @Valid @RequestBody final ApplicationRejectRequest request) {
    return ResponseEntity.ok(menteeAdminService.rejectMentee(menteeId, request.reason()));
  }
}
