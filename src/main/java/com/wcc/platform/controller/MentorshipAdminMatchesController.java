package com.wcc.platform.controller;

import com.wcc.platform.configuration.security.RequiresPermission;
import com.wcc.platform.configuration.security.RequiresRole;
import com.wcc.platform.domain.auth.Permission;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MatchCancelRequest;
import com.wcc.platform.domain.platform.mentorship.MenteeApplicationAdminResponse;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipMatch;
import com.wcc.platform.domain.platform.mentorship.recommendation.MentorshipRecommendationResponse;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.service.MenteeWorkflowService;
import com.wcc.platform.service.MentorshipCycleService;
import com.wcc.platform.service.MentorshipMatchingService;
import com.wcc.platform.service.MentorshipRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin controller for mentorship management operations. Handles match confirmation, cycle
 * management, and admin reporting.
 */
@RestController
@RequestMapping("/api/platform/v1/admin/mentorship")
@SecurityRequirement(name = "apiKey")
@Tag(name = "Platform: Mentorship Admin", description = "Admin endpoints for mentorship management")
@RequiredArgsConstructor
@SuppressWarnings("PMD.ExcessiveImports")
public class MentorshipAdminMatchesController {

  public static final String BEARER_AUTH = "bearerAuth";
  private final MentorshipMatchingService matchingService;
  private final MentorshipCycleRepository cycleRepository;
  private final MentorshipRecommendationService recommendationService;
  private final MenteeWorkflowService workflowService;
  private final MentorshipCycleService cycleService;

  // ==================== Match Management ====================

  /**
   * API to get suggested mentee matches for unmatched mentors.
   *
   * @return Recommended matches
   */
  @GetMapping("/matches/recommendations")
  @RequiresRole({RoleType.ADMIN, RoleType.LEADER})
  @Operation(
      summary = "Get suggested mentee matches for unmatched mentors",
      security = {@SecurityRequirement(name = BEARER_AUTH)})
  public ResponseEntity<MentorshipRecommendationResponse> getMatchRecommendations() {
    final var recommendations = recommendationService.getRecommendations();
    return ResponseEntity.ok(recommendations);
  }

  /**
   * API to get mentee applications for a specific cycle and statuses.
   *
   * @param cycleId the cycle ID
   * @param status list of application statuses to filter by
   * @param mentorId optional mentor ID filter
   * @return list of mentee applications
   */
  @GetMapping("/cycles/{cycleId}/applications")
  @RequiresPermission(Permission.MATCH_MANAGE)
  @Operation(
      summary = "Admin confirms a mentorship match from accepted application",
      security = {@SecurityRequirement(name = BEARER_AUTH)})
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<List<MenteeApplicationAdminResponse>> getApplications(
      @Parameter(description = "Cycle ID") @PathVariable final Long cycleId,
      @Parameter(description = "Application statuses") @RequestParam
          final List<ApplicationStatus> status,
      @Parameter(description = "Mentor ID filter") @RequestParam(required = false)
          final Long mentorId) {

    final var applications = workflowService.getApplicationsForAdmin(cycleId, status, mentorId);
    return ResponseEntity.ok(applications);
  }

  /**
   * API for admin to confirm a match from an accepted application. This creates the official
   * mentorship match record.
   *
   * @param applicationId The application ID
   * @return Created match
   */
  @PostMapping("/matches/confirm/{applicationId}")
  @RequiresPermission(Permission.MATCH_MANAGE)
  @Operation(
      summary = "Admin confirms a mentorship match from accepted application",
      security = {@SecurityRequirement(name = BEARER_AUTH)})
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<MentorshipMatch> confirmMatch(
      @Parameter(description = "Application ID to confirm as match") @PathVariable
          final Long applicationId) {
    final MentorshipMatch match = matchingService.confirmMatch(applicationId);
    return new ResponseEntity<>(match, HttpStatus.CREATED);
  }

  /**
   * API to get all matches for a specific cycle.
   *
   * @param cycleId The cycle ID
   * @return List of matches
   */
  @GetMapping("/matches")
  @RequiresPermission(Permission.MATCH_MANAGE)
  @Operation(
      summary = "Get all matches for a cycle",
      security = {@SecurityRequirement(name = BEARER_AUTH)})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<MentorshipMatch>> getCycleMatches(
      @Parameter(description = "Cycle ID") @RequestParam final Long cycleId) {
    final List<MentorshipMatch> matches = matchingService.getCycleMatches(cycleId);
    return ResponseEntity.ok(matches);
  }

  /**
   * API to complete a mentorship match.
   *
   * @param matchId The match ID
   * @param notes Optional completion notes
   * @return Updated match
   */
  @PatchMapping("/matches/{matchId}/complete")
  @RequiresPermission(Permission.MATCH_MANAGE)
  @Operation(
      summary = "Complete a mentorship match",
      security = {@SecurityRequirement(name = BEARER_AUTH)})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipMatch> completeMatch(
      @Parameter(description = "Match ID") @PathVariable final Long matchId,
      @Parameter(description = "Completion notes") @RequestParam(required = false)
          final String notes) {
    final MentorshipMatch updated = matchingService.completeMatch(matchId, notes);
    return ResponseEntity.ok(updated);
  }

  /**
   * API to cancel a mentorship match.
   *
   * @param matchId The match ID
   * @param request Cancellation request with reason and who canceled
   * @return Updated match
   */
  @PatchMapping("/matches/{matchId}/cancel")
  @RequiresPermission(Permission.MATCH_MANAGE)
  @Operation(
      summary = "Cancel a mentorship match",
      security = {@SecurityRequirement(name = BEARER_AUTH)})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipMatch> cancelMatch(
      @Parameter(description = "Match ID") @PathVariable final Long matchId,
      @Valid @RequestBody final MatchCancelRequest request) {
    final MentorshipMatch updated =
        matchingService.cancelMatch(matchId, request.reason(), request.cancelledBy());
    return ResponseEntity.ok(updated);
  }

  /**
   * API to increment session count for a match.
   *
   * @param matchId The match ID
   * @return Updated match
   */
  @PatchMapping("/matches/{matchId}/increment-session")
  @RequiresPermission(Permission.MATCH_MANAGE)
  @Operation(
      summary = "Increment session count for a match",
      security = {@SecurityRequirement(name = BEARER_AUTH)})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipMatch> incrementSessionCount(
      @Parameter(description = "Match ID") @PathVariable final Long matchId) {
    final MentorshipMatch updated = matchingService.incrementSessionCount(matchId);
    return ResponseEntity.ok(updated);
  }

  // ==================== Cycle Management ====================

  /**
   * API to get the currently open mentorship cycle.
   *
   * @return Current open cycle, or 404 if none is open
   */
  @GetMapping("/cycles/current")
  @RequiresRole({RoleType.ADMIN, RoleType.MENTORSHIP_ADMIN})
  @Operation(
      summary = "Get the currently open mentorship cycle",
      security = {@SecurityRequirement(name = BEARER_AUTH)})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipCycleEntity> getCurrentCycle() {
    return cycleRepository
        .findOpenCycle()
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * API to get all cycles by status.
   *
   * @param status The cycle status
   * @return List of cycles with the specified status
   */
  @GetMapping("/cycles")
  @RequiresRole({RoleType.ADMIN, RoleType.MENTORSHIP_ADMIN})
  @Operation(
      summary = "Get cycles by status",
      security = {@SecurityRequirement(name = BEARER_AUTH)})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<MentorshipCycleEntity>> getCyclesByStatus(
      @Parameter(description = "Cycle status") @RequestParam final CycleStatus status) {
    final List<MentorshipCycleEntity> cycles = cycleRepository.findByStatus(status);
    return ResponseEntity.ok(cycles);
  }

  /**
   * API to get a specific cycle by ID.
   *
   * @param cycleId The cycle ID
   * @return The cycle, or 404 if not found
   */
  @GetMapping("/cycles/{cycleId}")
  @RequiresRole({RoleType.ADMIN, RoleType.MENTORSHIP_ADMIN})
  @Operation(
      summary = "Get a cycle by ID",
      security = {@SecurityRequirement(name = BEARER_AUTH)})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipCycleEntity> getCycleById(
      @Parameter(description = "Cycle ID") @PathVariable final Long cycleId) {
    return cycleRepository
        .findById(cycleId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * API to get all mentorship cycles.
   *
   * @return List of all cycles
   */
  @GetMapping("/cycles/all")
  @RequiresRole({RoleType.ADMIN, RoleType.MENTORSHIP_ADMIN})
  @Operation(
      summary = "Get all mentorship cycles",
      security = {@SecurityRequirement(name = BEARER_AUTH)})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<MentorshipCycleEntity>> getAllCycles() {
    final List<MentorshipCycleEntity> cycles = cycleRepository.getAll();
    return ResponseEntity.ok(cycles);
  }

  /**
   * API to update the status of a mentorship cycle.
   *
   * @param cycleId the ID of the cycle to update
   * @param request the status update request containing the new status
   * @return the updated cycle entity
   */
  @PatchMapping("/cycles/status")
  @RequiresRole({RoleType.ADMIN, RoleType.MENTORSHIP_ADMIN})
  @Operation(
      summary = "Update the status of a mentorship cycle",
      security = {@SecurityRequirement(name = BEARER_AUTH)})
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipCycleEntity> updateCycleStatus(
      @Parameter(description = "Cycle ID") @RequestParam final Long cycleId,
      @Parameter(description = "New cycle status") @RequestParam final CycleStatus status) {
    final MentorshipCycleEntity updated = cycleService.updateStatus(cycleId, status);
    return ResponseEntity.ok(updated);
  }
}
