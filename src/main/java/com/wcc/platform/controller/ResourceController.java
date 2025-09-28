package com.wcc.platform.controller;

import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Controller for managing resources and profile pictures. */
@RestController
@RequestMapping("/api/platform/v1/resources")
@SecurityRequirement(name = "apiKey")
@Tag(name = "Resources", description = "APIs for managing resources and profile pictures")
@AllArgsConstructor
public class ResourceController {

  private final ResourceService resourceService;

  /** Uploads a resource. */
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload a resource")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Resource> uploadResource(
      @Parameter(description = "File to upload") @RequestParam("file") final MultipartFile file,
      @Parameter(description = "Name of the resource") @RequestParam("name") final String name,
      @Parameter(description = "Description of the resource")
          @RequestParam(value = "description", required = false)
          final String description,
      @Parameter(description = "Type of resource") @RequestParam("resourceType")
          final ResourceType resourceType) {

    final Resource resource = resourceService.uploadResource(file, name, description, resourceType);
    return new ResponseEntity<>(resource, HttpStatus.CREATED);
  }

  /** Gets a resource by ID. */
  @GetMapping("/{id}")
  @Operation(summary = "Get a resource by ID")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Resource> getResource(
      @Parameter(description = "ID of the resource") @PathVariable final UUID id) {

    final Resource resource = resourceService.getResource(id);
    return ResponseEntity.ok(resource);
  }

  /** Gets resources by type. */
  @GetMapping
  @Operation(summary = "Get resources by type")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<Resource>> getResourcesByType(
      @Parameter(description = "Type of resources to get") @RequestParam
          final ResourceType resourceType) {

    final List<Resource> resources = resourceService.getResourcesByType(resourceType);
    return ResponseEntity.ok(resources);
  }

  /** Searches for resources by name. */
  @GetMapping("/search")
  @Operation(summary = "Search for resources by name")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<Resource>> searchResourcesByName(
      @Parameter(description = "Name to search for") @RequestParam final String name) {

    final List<Resource> resources = resourceService.searchResourcesByName(name);
    return ResponseEntity.ok(resources);
  }

  /** Deletes a resource. */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a resource")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> deleteResource(
      @Parameter(description = "ID of the resource to delete") @PathVariable final UUID id) {

    resourceService.deleteResource(id);
    return ResponseEntity.noContent().build();
  }

  /** Uploads a member's profile picture. */
  @PostMapping(value = "/mentor-profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload a member's profile picture")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<MemberProfilePicture> uploadMentorProfilePicture(
      @Parameter(description = "Id of the member") @RequestParam final Integer memberId,
      @Parameter(description = "Profile picture file") @RequestParam("file")
          final MultipartFile file) {

    final var profilePicture = resourceService.uploadMentorProfilePicture(memberId, file);
    return new ResponseEntity<>(profilePicture, HttpStatus.CREATED);
  }

  /** Gets a mentor's profile picture. */
  @GetMapping("/mentor-profile-picture/{memberId}")
  @Operation(summary = "Get a member's profile picture")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MemberProfilePicture> getMentorProfilePicture(
      @Parameter(description = "Id of the member") @PathVariable final Integer memberId) {

    final var profilePicture = resourceService.getMemberProfilePicture(memberId);
    return ResponseEntity.ok(profilePicture);
  }

  /** Deletes a mentor's profile picture. */
  @DeleteMapping("/mentor-profile-picture/{memberId}")
  @Operation(summary = "Delete a member's profile picture")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> deleteMemberProfilePicture(
      @Parameter(description = "Id of the member") @PathVariable final Integer memberId) {

    resourceService.deleteMemberProfilePicture(memberId);
    return ResponseEntity.noContent().build();
  }
}
