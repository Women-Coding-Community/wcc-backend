package com.wcc.platform.controller;

import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.MenteeRegistration;
import com.wcc.platform.service.MenteeService;
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
   * Retrieves a list of all registered mentees.
   *
   * @return a list of Mentee existent mentees
   */
  @GetMapping("/mentees")
  @Operation(summary = "API to list all mentees")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<Mentee>> listMentees() {
    return new ResponseEntity<>(menteeService.getAllMentees(), HttpStatus.OK);
  }
}
