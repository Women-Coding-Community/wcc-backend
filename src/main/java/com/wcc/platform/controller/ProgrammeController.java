package com.wcc.platform.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for all the programme apis */
@RestController
@RequestMapping("/api/cms/v1/programme")
@Tag(name = "APIs relevant to Programme pages")
public class ProgrammeController {}
