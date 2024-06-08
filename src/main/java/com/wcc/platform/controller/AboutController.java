package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.service.CmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cms/v1/")
@Api(value = "APIs relevant About Us section")
public class AboutController {

    private final CmsService cmsService;

    @Autowired
    public AboutController(CmsService service) {
        this.cmsService = service;
    }

    @GetMapping("/team")
    @ApiOperation(value = "API to retrieve information about leadership team members")
    public ResponseEntity<TeamPage> getTeamPage() {
        return ResponseEntity.ok(cmsService.getTeam());
    }
}
