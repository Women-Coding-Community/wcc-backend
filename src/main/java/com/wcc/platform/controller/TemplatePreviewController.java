package com.wcc.platform.controller;

import com.wcc.platform.domain.platform.email.templates.RenderedTemplate;
import com.wcc.platform.domain.platform.email.templates.TemplateRequest;
import com.wcc.platform.domain.platform.email.templates.service.TemplateWriterService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/platform/v1")
@SecurityRequirement(name = "apiKey")
@Tag(name = "Email Templates", description = "All email templates internal APIs")
@AllArgsConstructor
public class TemplatePreviewController {
  private final TemplateWriterService writer;

  @PostMapping("/preview")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<RenderedTemplate> preview(@RequestBody final TemplateRequest req) {
    RenderedTemplate renderedTemplate = writer.render(req);
    return new ResponseEntity<>(renderedTemplate, HttpStatus.CREATED);
  }
}
