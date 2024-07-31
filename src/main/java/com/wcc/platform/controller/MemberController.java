package com.wcc.platform.controller;

import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.service.PlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for members pages apis. */
@RestController
@RequestMapping("/api/platform/v1")
@Tag(name = "APIs relevant Members section")
public class MemberController {

  private final PlatformService service;

  @Autowired
  public MemberController(final PlatformService service) {
    this.service = service;
  }

  /**
   * API to create member.
   *
   * @return Created new Member content.
   */
  @PutMapping("/member")
  @Operation(summary = "API to submit member registration")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Member> createMember(@RequestBody final Member member) {
    return ResponseEntity.ok(service.createMember(member));
  }
}
