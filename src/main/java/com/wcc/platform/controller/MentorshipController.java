package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.cms.pages.mentorship.LongTermTimeLinePage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorAppliedFilters;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipAdHocTimelinePage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipCodeOfConductPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipFaqPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipStudyGroupsPage;
import com.wcc.platform.domain.platform.mentorship.MentorshipType;
import com.wcc.platform.service.MentorshipPagesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for mentorship apis. */
@RestController
@RequestMapping("/api/cms/v1/mentorship")
@SecurityRequirement(name = "apiKey")
@Tag(name = "Pages: Mentorship", description = "All APIs under session Mentorship")
public class MentorshipController {

  private final MentorshipPagesService service;

  @Autowired
  public MentorshipController(final MentorshipPagesService service) {
    this.service = service;
  }

  @GetMapping("/overview")
  @Operation(summary = "API to retrieve mentorship overview page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipPage> getMentorshipOverview() {
    return ResponseEntity.ok(service.getOverview());
  }

  @GetMapping("/faq")
  @Operation(summary = "API to retrieve mentorship faq page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipFaqPage> getMentorshipFaq() {
    return ResponseEntity.ok(service.getFaq());
  }

  @GetMapping("/long-term-timeline")
  @Operation(summary = "API to retrieve timeline for long-term mentorship")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<LongTermTimeLinePage> getMentorshipLongTermTimeLine() {
    return ResponseEntity.ok(service.getLongTermTimeLine());
  }

  @GetMapping("/code-of-conduct")
  @Operation(summary = "API to retrieve mentorship code of conduct page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipCodeOfConductPage> getMentorshipCodeOfConduct() {
    return ResponseEntity.ok(service.getCodeOfConduct());
  }

  @GetMapping("/study-groups")
  @Operation(summary = "API to retrieve mentorship study groups page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipStudyGroupsPage> getMentorshipStudyGroup() {
    return ResponseEntity.ok(service.getStudyGroups());
  }

  /**
   * Retrieves a paginated list of mentors based on the specified filters.
   *
   * @param keyword an optional search keyword to filter by mentor name or description
   * @param mentorshipTypes an optional list of mentorship types to filter mentors by
   * @param yearsExperience an optional number to filter mentors by minimum years of experience
   * @param areas an optional list of technical areas to filter mentors by
   * @param languages an optional list of languages to filter mentors by
   * @param focus an optional list of focus areas to filter mentors by
   * @return a {@code ResponseEntity} containing a {@code MentorsPage} object with the filtered list
   *     of mentors
   */
  @GetMapping("/mentors")
  @Operation(summary = "API to retrieve mentors page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorsPage> getMentors(
      final @RequestParam(required = false) String keyword,
      final @RequestParam(required = false) List<MentorshipType> mentorshipTypes,
      final @RequestParam(required = false) Integer yearsExperience,
      final @RequestParam(required = false) List<TechnicalArea> areas,
      final @RequestParam(required = false) List<Languages> languages,
      final @RequestParam(required = false) List<MentorshipFocusArea> focus) {
    final var filters =
        new MentorAppliedFilters(
            keyword, mentorshipTypes, yearsExperience, areas, languages, focus);
    return ResponseEntity.ok(service.getMentorsPage(filters));
  }

  @GetMapping("/ad-hoc-timeline")
  @Operation(summary = "API to retrieve ad hoc timeline page")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MentorshipAdHocTimelinePage> getAdHocTimeline() {
    return ResponseEntity.ok(service.getAdHocTimeline());
  }
}
