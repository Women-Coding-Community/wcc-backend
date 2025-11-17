package com.wcc.platform.controller;

import com.wcc.platform.domain.template.RenderedTemplate;
import com.wcc.platform.domain.template.TemplateRequest;
import com.wcc.platform.service.EmailTemplateService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/platform/v1/email/template")
@SecurityRequirement(name = "apiKey")
@Tag(name = "Platform: Email Template", description = "Platform Internal APIs")
@RequiredArgsConstructor
public class EmailTemplateController {

  private final EmailTemplateService emailTemplateService;

  @PostMapping("/preview")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<RenderedTemplate> preview(@RequestBody final TemplateRequest request) {
    final RenderedTemplate renderedTemplate =
        emailTemplateService.renderTemplate(request.getTemplateType(), request.getParams());
    return new ResponseEntity<>(renderedTemplate, HttpStatus.CREATED);
  }
}
