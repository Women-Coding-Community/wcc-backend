package com.wcc.platform.controller;

import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeRegistration;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.service.MenteeService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for members pages apis. */
@RestController
@RequestMapping("/api/platform/v1")
@SecurityRequirement(name = "apiKey")
@Tag(name = "Platform", description = "All platform Internal APIs")
@AllArgsConstructor
@Validated
public class MentorshipController {

  private final MentorshipService mentorshipService;
  private final MenteeService menteeService;

  /**
   * API to retrieve information about mentors.
   *
   * @return List of all mentors.
   */
  @GetMapping("/mentors")
  @Operation(summary = "API to retrieve a list of all members")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<MentorDto>> getAllMentors() {
    final List<MentorDto> mentors = mentorshipService.getAllMentors();
    return ResponseEntity.ok(mentors);
  }

  /**
   * API to create mentor.
   *
   * @return Create a new mentor.
   */
  @PostMapping("/mentors")
  @Operation(summary = "API to submit mentor registration")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Mentor> createMentor(@Valid @RequestBody final Mentor mentor) {
    return new ResponseEntity<>(mentorshipService.create(mentor), HttpStatus.CREATED);
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
      @PathVariable final Long mentorId, @RequestBody final MentorDto mentorDto) {
    return new ResponseEntity<>(mentorshipService.updateMentor(mentorId, mentorDto), HttpStatus.OK);
  }

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
      @RequestBody final MenteeRegistration menteeRegistration) {
    return new ResponseEntity<>(
        menteeService.saveRegistration(menteeRegistration), HttpStatus.CREATED);
  }
}
