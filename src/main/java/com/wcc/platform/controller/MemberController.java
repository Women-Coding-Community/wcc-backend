package com.wcc.platform.controller;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import com.wcc.platform.domain.platform.mentorship.MentorDto;
import com.wcc.platform.service.MemberService;
import com.wcc.platform.service.MentorshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class MemberController {

  private final MemberService memberService;
  private final MentorshipService mentorshipService;

  /**
   * API to retrieve information about members.
   *
   * @return List of all members.
   */
  @GetMapping("/members")
  @Operation(summary = "API to retrieve a list of all members")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<Member>> getAllMembers() {
    final List<Member> members = memberService.getAllMembers();
    return ResponseEntity.ok(members);
  }

  /**
   * API to retrieve information users with access to platform restrict area.
   *
   * @return List of all members.
   */
  @GetMapping("/users")
  @Operation(summary = "API to retrieve users with access to restrict area")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<UserAccount>> getUsers() {
    return ResponseEntity.ok(memberService.getUsers());
  }

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
   * API to create member.
   *
   * @return Create a new member.
   */
  @PostMapping("/members")
  @Operation(summary = "API to submit member registration")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Member> createMember(@RequestBody final Member member) {
    return new ResponseEntity<>(memberService.createMember(member), HttpStatus.CREATED);
  }

  /**
   * API to create mentor.
   *
   * @return Create a new mentor.
   */
  @PostMapping("/mentors")
  @Operation(summary = "API to submit mentor registration")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Mentor> createMentor(@RequestBody final Mentor mentor) {
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
  public ResponseEntity<Member> updateMentor(
      @PathVariable final Long mentorId, @RequestBody final MentorDto mentorDto) {
    return new ResponseEntity<>(mentorshipService.updateMentor(mentorId, mentorDto), HttpStatus.OK);
  }

  /**
   * API to update member information.
   *
   * @param memberId member's unique identifier
   * @param memberDto MemberDto with updated member's data
   * @return Updated member
   */
  @PutMapping("/members/{memberId}")
  @Operation(summary = "API to update member data")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Member> updateMember(
      @PathVariable final Long memberId, @RequestBody final MemberDto memberDto) {
    return new ResponseEntity<>(memberService.updateMember(memberId, memberDto), HttpStatus.OK);
  }

  /** Deletes a member. */
  @DeleteMapping("/members/{memberId}")
  @Operation(summary = "Delete a member")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> deleteMember(
      @Parameter(description = "ID of the member to delete") @PathVariable final Long memberId) {
    memberService.deleteMember(memberId);
    return ResponseEntity.noContent().build();
  }
}
