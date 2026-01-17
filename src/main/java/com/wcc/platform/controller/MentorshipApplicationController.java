package com.wcc.platform.controller;

import com.wcc.platform.domain.platform.mentorship.ApplicationAcceptRequest;
import com.wcc.platform.domain.platform.mentorship.ApplicationDeclineRequest;
import com.wcc.platform.domain.platform.mentorship.ApplicationStatus;
import com.wcc.platform.domain.platform.mentorship.ApplicationSubmitRequest;
import com.wcc.platform.domain.platform.mentorship.ApplicationWithdrawRequest;
import com.wcc.platform.domain.platform.mentorship.MenteeApplication;
import com.wcc.platform.service.MenteeApplicationService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for members pages apis. */
@RestController
@RequestMapping("/api/platform/v1")
@SecurityRequirement(name = "apiKey")
@Tag(name = "Platform", description = "All platform Internal APIs")
@AllArgsConstructor
@Validated
public class MentorshipApplicationController {

  private final MenteeApplicationService applicationService;

  /**
   * API for mentee to submit applications to multiple mentors with priority ranking.
   *
   * @param menteeId The mentee ID
   * @param request Application submission request
   * @return List of created applications
   */
  @PostMapping("/mentees/{menteeId}/applications")
  @Operation(summary = "Submit mentee applications to mentors with priority ranking")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<List<MenteeApplication>> submitApplications(
      @Parameter(description = "ID of the mentee") @PathVariable final Long menteeId,
      @Valid @RequestBody final ApplicationSubmitRequest request) {
    final List<MenteeApplication> applications =
        applicationService.submitApplications(
            menteeId, request.cycleId(), request.mentorIds(), request.message());
    return new ResponseEntity<>(applications, HttpStatus.CREATED);
  }

  /**
   * API to get all applications submitted by a mentee for a specific cycle.
   *
   * @param menteeId The mentee ID
   * @param cycleId The cycle ID
   * @return List of applications ordered by priority
   */
  @GetMapping("/mentees/{menteeId}/applications")
  @Operation(summary = "Get mentee applications for a cycle")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<MenteeApplication>> getMenteeApplications(
      @Parameter(description = "ID of the mentee") @PathVariable final Long menteeId,
      @Parameter(description = "Cycle ID") @RequestParam final Long cycleId) {
    final List<MenteeApplication> applications =
        applicationService.getMenteeApplications(menteeId, cycleId);
    return ResponseEntity.ok(applications);
  }

  /**
   * API for mentee to withdraw an application.
   *
   * @param applicationId The application ID
   * @param request Withdrawal request with reason
   * @return Updated application
   */
  @PatchMapping("/mentees/applications/{applicationId}/withdraw")
  @Operation(summary = "Mentee withdraws an application")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MenteeApplication> withdrawApplication(
      @Parameter(description = "Application ID") @PathVariable final Long applicationId,
      @Valid @RequestBody final ApplicationWithdrawRequest request) {
    final MenteeApplication updated =
        applicationService.withdrawApplication(applicationId, request.reason());
    return ResponseEntity.ok(updated);
  }

  /**
   * API to get all applications received by a mentor.
   *
   * @param mentorId The mentor ID
   * @param status Optional filter by application status
   * @return List of applications
   */
  @GetMapping("/mentors/{mentorId}/applications")
  @Operation(summary = "Get applications received by a mentor")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<MenteeApplication>> getMentorApplications(
      @Parameter(description = "ID of the mentor") @PathVariable final Long mentorId,
      @Parameter(description = "Filter by status (optional)") @RequestParam(required = false)
          final ApplicationStatus status) {
    final List<MenteeApplication> applications = applicationService.getMentorApplications(mentorId);

    // Filter by status if provided
    final List<MenteeApplication> filtered =
        status != null
            ? applications.stream().filter(app -> app.getStatus() == status).toList()
            : applications;

    return ResponseEntity.ok(filtered);
  }

  /**
   * API for mentor to accept an application.
   *
   * @param applicationId The application ID
   * @param request Accept request with optional mentor response
   * @return Updated application
   */
  @PatchMapping("/mentors/applications/{applicationId}/accept")
  @Operation(summary = "Mentor accepts an application")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MenteeApplication> acceptApplication(
      @Parameter(description = "Application ID") @PathVariable final Long applicationId,
      @Valid @RequestBody final ApplicationAcceptRequest request) {
    final MenteeApplication updated =
        applicationService.acceptApplication(applicationId, request.mentorResponse());
    return ResponseEntity.ok(updated);
  }

  /**
   * API for mentor to decline an application.
   *
   * @param applicationId The application ID
   * @param request Decline request with reason
   * @return Updated application
   */
  @PatchMapping("/mentors/applications/{applicationId}/decline")
  @Operation(summary = "Mentor declines an application")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MenteeApplication> declineApplication(
      @Parameter(description = "Application ID") @PathVariable final Long applicationId,
      @Valid @RequestBody final ApplicationDeclineRequest request) {
    final MenteeApplication updated =
        applicationService.declineApplication(applicationId, request.reason());
    return ResponseEntity.ok(updated);
  }

  /**
   * API to get applications by status (useful for admin/reporting).
   *
   * @param status The application status to filter by
   * @return List of applications with the specified status
   */
  @GetMapping("/applications")
  @Operation(summary = "Get all applications by status")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<MenteeApplication>> getApplicationsByStatus(
      @Parameter(description = "Application status") @RequestParam final ApplicationStatus status) {
    final List<MenteeApplication> applications = applicationService.getApplicationsByStatus(status);
    return ResponseEntity.ok(applications);
  }
}
