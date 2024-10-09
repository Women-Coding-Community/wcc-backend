package com.wcc.platform.controller;

import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberDto;
import com.wcc.platform.service.PlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/platform/v1/members")
@Tag(name = "APIs relevant Members section")
public class MemberController {

  private final PlatformService platformService;

  @Autowired
  public MemberController(final PlatformService service) {
    this.platformService = service;
  }

  /**
   * API to retrieve information about members.
   *
   * @return List of all members.
   */
  @GetMapping
  @Operation(summary = "API to retrieve a list of all members")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<Member>> getAllMembers() {
    final List<Member> members = platformService.getAll();
    return ResponseEntity.ok(members);
  }

  /**
   * API to create member.
   *
   * @return Create a new member.
   */
  @PostMapping
  @Operation(summary = "API to submit member registration")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Member> createMember(@RequestBody final Member member) {
    return new ResponseEntity<>(platformService.createMember(member), HttpStatus.CREATED);
  }

  /**
   * API to update member information.
   *
   * @param email member's email as unique identifier
   * @param memberDto MemberDto with updated member's data
   * @return Updated member
   */
  @PutMapping("/{email}")
  @Operation(summary = "API to update member data")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Member> updateMember(
      @PathVariable String email, @RequestBody final MemberDto memberDto) {
    return new ResponseEntity<>(platformService.updateMember(email, memberDto), HttpStatus.OK);
  }
}
