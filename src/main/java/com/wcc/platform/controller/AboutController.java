package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.service.CmsService;
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
@RequestMapping("/api/cms/v1/")
@Tag(name = "APIs relevant About Us section")
public class AboutController {

    private final CmsService cmsService;

    @Autowired
    public AboutController(CmsService service) {
        this.cmsService = service;
    }

    @GetMapping("/team")
    @Operation(summary = "API to retrieve information about leadership team members")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TeamPage> getTeamPage() {
        return ResponseEntity.ok(cmsService.getTeam());
    }

    @GetMapping("/collaborators")
    @ApiOperation(value = "API to retrieve information about collaborators")
    public ResponseEntity<CollaboratorPage> getCollaboratorPage() {
        return ResponseEntity.ok(cmsService.getCollaborator());
    }
}
