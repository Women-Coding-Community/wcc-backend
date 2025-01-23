package com.wcc.platform.controller.platform;

import com.wcc.platform.domain.platform.ResourceContent;
import com.wcc.platform.service.PlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for event pages API. */
@RestController
@RequestMapping("/api/platform/v1/")
@SecurityRequirement(name = "apiKey")
@Tag(name = "Platform", description = "All platform Internal APIs")
public class ResourceContentController {

  private final PlatformService service;

  @Autowired
  public ResourceContentController(final PlatformService service) {
    this.service = service;
  }

  /** Put resources content for a program. */
  @PutMapping("/resource")
  @Operation(summary = "Save resource")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<ResourceContent> addResource(@RequestBody final ResourceContent resource) {
    return ResponseEntity.ok(service.saveResourceContent(resource));
  }

  /** Get all resources content. */
  @GetMapping("/resource")
  @Operation(summary = "Get Resource by ID")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<ResourceContent> getResourceById(@RequestParam final String id) {
    return ResponseEntity.ok(service.getResourceById(id));
  }

  /** Delete resource content if exists. */
  @DeleteMapping("/resource/{id}")
  @Operation(summary = "Delete resource content if exists")
  @ResponseStatus(HttpStatus.OK)
  public void deleteResourceById(@PathVariable final String id) {
    service.deleteById(id);
  }
}
