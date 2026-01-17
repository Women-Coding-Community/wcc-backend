package com.wcc.platform.controller;

import com.wcc.platform.domain.platform.mentorship.CycleStatus;
import com.wcc.platform.domain.platform.mentorship.MatchCancelRequest;
import com.wcc.platform.domain.platform.mentorship.MentorshipCycleEntity;
import com.wcc.platform.domain.platform.mentorship.MentorshipMatch;
import com.wcc.platform.repository.MentorshipCycleRepository;
import com.wcc.platform.service.MentorshipMatchingService;
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
public class AdminMentorshipController {

  private final MentorshipMatchingService matchingService;
  private final MentorshipCycleRepository cycleRepository;

  // ==================== Match Management ====================

  /**
   * API for admin to confirm a match from an accepted application. This creates the official
   * mentorship match record.
   *
   * @param applicationId The application ID
   * @return Created match
   */
  @PostMapping("/matches/confirm/{applicationId}")
  @Operation(summary = "Admin confirms a mentorship match from accepted application")
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
  @Operation(summary = "Get all matches for a cycle")
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
  @Operation(summary = "Complete a mentorship match")
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
   * @param request Cancellation request with reason and who cancelled
   * @return Updated match
   */
  @PatchMapping("/matches/{matchId}/cancel")
  @Operation(summary = "Cancel a mentorship match")
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
  @Operation(summary = "Increment session count for a match")
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
  @Operation(summary = "Get the currently open mentorship cycle")
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
  @Operation(summary = "Get cycles by status")
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
  @Operation(summary = "Get a cycle by ID")
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
  @Operation(summary = "Get all mentorship cycles")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<MentorshipCycleEntity>> getAllCycles() {
    final List<MentorshipCycleEntity> cycles = cycleRepository.getAll();
    return ResponseEntity.ok(cycles);
  }
}
