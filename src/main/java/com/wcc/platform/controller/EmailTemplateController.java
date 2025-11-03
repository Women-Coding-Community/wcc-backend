package com.wcc.platform.controller;

import com.wcc.platform.domain.template.RenderedTemplate;
import com.wcc.platform.domain.template.TemplateRequest;
import com.wcc.platform.service.EmailTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/platform/v1")
@Tag(name = "Platform: Email Template", description = "Platform Internal APIs")
@RequiredArgsConstructor
public class EmailTemplateController {

  private final EmailTemplateService emailTemplateService;

  @PostMapping("/preview")
  public ResponseEntity<RenderedTemplate> preview(@RequestBody final TemplateRequest request) {
    log.debug("Received template preview request: {}", request);
    RenderedTemplate renderedTemplate =
        emailTemplateService.renderTemplate(request.getTemplateType(), request.getParams());
    return ResponseEntity.ok(renderedTemplate);
  }
}
