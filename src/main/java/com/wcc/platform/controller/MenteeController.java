package com.wcc.platform.controller;

import com.wcc.platform.configuration.security.RequiresPermission;
import com.wcc.platform.configuration.security.RequiresRole;
import com.wcc.platform.domain.auth.Permission;
import com.wcc.platform.domain.platform.mentorship.ApplicationRejectRequest;
import com.wcc.platform.domain.platform.mentorship.AssignMentorRequest;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.domain.platform.mentorship.MenteeRegistration;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.domain.platform.mentorship.NoMatchRequest;
import com.wcc.platform.service.MenteeAdminService;
import com.wcc.platform.service.MenteeService;
import com.wcc.platform.service.MenteeWorkflowService;
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
import org.springframework.web.bind.annotation.*;

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
  private final MenteeWorkflowService menteeWorkflowService;

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
  @RequiresRole({RoleType.ADMIN, RoleType.MENTORSHIP_ADMIN, RoleType.LEADER})
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

  /**
   * API to get all mentees for a cycle whose application is PENDING_MANUAL_MATCH
   *
   * @return List of mentees
   */
  @GetMapping("/mentees/pending-manual-match")
  @Operation(
      summary = "Get all mentees for a cycle whose application pending manual match",
      security = {@SecurityRequirement(name = "apiKey"), @SecurityRequirement(name = "bearerAuth")})
  @RequiresPermission(Permission.MENTEE_APPROVE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<Mentee>> getMenteesPendingManualMatch(
      @Parameter(description = "Cycle ID (optional)") @RequestParam(required = false)
          final Long cycleId) {
    final List<Mentee> matches = menteeService.getMenteePendingManualMatch(cycleId);
    return ResponseEntity.ok(matches);
  }

  /**
   * Manually assigns a mentor to a mentee from the manual match queue.
   *
   * @param menteeId the mentee ID
   * @param cycleId the cycle ID
   * @param request the assignment request containing mentor ID and notes
   * @return the newly created application
   */
  @PostMapping("/mentees/{menteeId}/cycles/{cycleId}/assign-mentor")
  @RequiresPermission(Permission.MENTEE_APPROVE)
  @Operation(
      summary = "Manually assign a mentor to a mentee from the manual match queue",
      security = {@SecurityRequirement(name = "apiKey"), @SecurityRequirement(name = "bearerAuth")})
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<MenteeApplication> assignMentor(
      @Parameter(description = "Mentee ID") @PathVariable final Long menteeId,
      @Parameter(description = "Cycle ID") @PathVariable final Long cycleId,
      @Valid @RequestBody final AssignMentorRequest request) {
    final MenteeApplication application =
        menteeWorkflowService.assignMentor(menteeId, cycleId, request.mentorId(), request.notes());
    return new ResponseEntity<>(application, HttpStatus.CREATED);
  }

  /**
   * Confirms no match was found for a mentee in the manual match queue. This is a terminal state.
   *
   * @param menteeId the mentee ID
   * @param cycleId the cycle ID
   * @param request the request containing the reason
   * @return the updated application
   */
  @PostMapping("/mentees/{menteeId}/cycles/{cycleId}/no-match")
  @RequiresPermission(Permission.MENTEE_APPROVE)
  @Operation(
      summary = "Confirm no match found for a mentee (terminal state)",
      security = {@SecurityRequirement(name = "apiKey"), @SecurityRequirement(name = "bearerAuth")})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MenteeApplication> confirmNoMatch(
      @Parameter(description = "Mentee ID") @PathVariable final Long menteeId,
      @Parameter(description = "Cycle ID") @PathVariable final Long cycleId,
      @Valid @RequestBody final NoMatchRequest request) {
    final MenteeApplication application =
        menteeWorkflowService.confirmNoMatch(menteeId, cycleId, request.reason());
    return ResponseEntity.ok(application);
  }
}
