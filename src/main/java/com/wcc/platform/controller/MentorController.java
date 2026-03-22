package com.wcc.platform.controller;

import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.domain.platform.mentorship.MentorRejectionRequest;
import com.wcc.platform.service.MentorshipService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for mentors page apis. */
@RestController
@RequestMapping("/api/platform/v1")
@SecurityRequirement(name = "apiKey")
@Tag(
    name = "Platform: Mentors",
    description = "APIs for mentor registration, approval, and management")
@AllArgsConstructor
@Validated
public class MentorController {

  private final MentorshipService mentorshipService;

  /**
   * API to retrieve information about mentors.
   *
   * @return List of all mentors.
   */
  @GetMapping("/mentors")
  @Operation(summary = "API to retrieve a list of all mentors")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<MentorDto>> getAllMentors() {
    final List<MentorDto> mentors = mentorshipService.getAllActiveMentors();
    return ResponseEntity.ok(mentors);
  }

  /**
   * API to create mentor. Profile status is set by the server to PENDING; any value in the request
   * is ignored.
   *
   * @param mentorDto mentor registration payload for request
   * @return Created mentor with status PENDING.
   */
  @PostMapping("/mentors")
  @Operation(summary = "API to submit mentor registration")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Mentor> createMentor(@Valid @RequestBody final MentorDto mentorDto) {
    return new ResponseEntity<>(mentorshipService.create(mentorDto.toMentor()), HttpStatus.CREATED);
  }

  /**
   * API to update mentor information.
   *
   * @param mentorId mentor's unique identifier
   * @param mentorDto MentorDto with updated mentor's data
   * @return Updated mentor
   */
  @PutMapping("/mentors/{mentorId}")
  @Operation(summary = "API to update mentor data")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Mentor> updateMentor(
      @Valid @PathVariable final Long mentorId, @RequestBody final MentorDto mentorDto) {
    return new ResponseEntity<>(mentorshipService.updateMentor(mentorId, mentorDto), HttpStatus.OK);
  }

  /**
   * API to accept mentor registration.
   *
   * @param mentorId mentor's unique identifier
   * @return updated mentor with active status.
   */
  @PatchMapping("/mentors/{mentorId}/accept")
  @Operation(summary = "API to accept mentor registration")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Mentor> acceptMentor(@Valid @PathVariable final Long mentorId) {
    return new ResponseEntity<>(mentorshipService.activateMentor(mentorId), HttpStatus.OK);
  }

  /**
   * API to reject mentor registration.
   *
   * @param mentorId mentor's unique identifier
   * @return updated mentor with rejected status.
   */
  @PatchMapping("/mentors/{mentorId}/reject")
  @Operation(summary = "API to reject mentor registration")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Mentor> rejectMentor(
      @Valid @PathVariable final Long mentorId,
      @RequestBody final MentorRejectionRequest rejectionRequest) {
    return new ResponseEntity<>(
        mentorshipService.rejectMentor(mentorId, rejectionRequest.reason()), HttpStatus.OK);
  }
}
