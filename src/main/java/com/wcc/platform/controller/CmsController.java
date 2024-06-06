package com.wcc.platform.controller;

import com.wcc.platform.domain.pages.TeamPage;
import com.wcc.platform.domain.pages.CodeOfConductPage;
import com.wcc.platform.service.CmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cms/v1/")

public class CmsController {

    private final CmsService cmsService;

    @Autowired
    public CmsController(CmsService service) {
        this.cmsService = service;
    }

    @GetMapping("/team")
    public TeamPage getTeamPage() {
        return cmsService.getTeam();
    }

    @GetMapping("/code_of_conduct")
    public CodeOfConductPage getCodeOfConductPage() {
        return cmsService.getCodeOfConduct();
    }
}
