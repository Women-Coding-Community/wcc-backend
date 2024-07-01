package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.service.MentorshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cms/v1/mentorship")
@Tag(name = "APIs relevant Mentorship pages")
public class MentorshipController {

    private final MentorshipService service;

    @Autowired
    public MentorshipController(MentorshipService service) {
        this.service = service;
    }

    @GetMapping("/overview")
    @Operation(summary = "API to retrieve mentorship overview page")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MentorshipPage> getMentorshipOverview() {
        return ResponseEntity.ok(service.getOverview());
    }

}
