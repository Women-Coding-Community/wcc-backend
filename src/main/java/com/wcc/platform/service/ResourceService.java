package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.ResourceNotFoundException;
import com.wcc.platform.domain.resource.MentorProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.domain.resource.ResourceType;
import com.wcc.platform.repository.MentorProfilePictureRepository;
import com.wcc.platform.repository.ResourceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/** Service for managing resources and profile pictures. */
@Slf4j
@Service
@AllArgsConstructor
public class ResourceService {

  private final ResourceRepository resourceRepo;
  private final MentorProfilePictureRepository profilePicRepo;
  private final GoogleDriveService driveService;

  /** Uploads a resource to Google Drive and stores its metadata in the database. */
  @Transactional
  public Resource uploadResource(
      final MultipartFile file,
      final String name,
      final String description,
      final ResourceType resourceType) {

    final var driveFile = driveService.uploadFile(file);

    final Resource resource =
        Resource.builder()
            .id(UUID.randomUUID())
            .name(name)
            .description(description)
            .fileName(file.getOriginalFilename())
            .contentType(file.getContentType())
            .size(file.getSize())
            .driveFileId(driveFile.getId())
            .driveFileLink(driveFile.getWebViewLink())
            .resourceType(resourceType)
            .build();

    return resourceRepo.create(resource);
  }

  /** Gets a resource by ID. */
  public Resource getResource(final UUID id) {
    return resourceRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
  }

  /** Gets resources by type. */
  public List<Resource> getResourcesByType(final ResourceType resourceType) {
    return resourceRepo.findByType(resourceType);
  }

  /** Searches for resources by name. */
  public List<Resource> searchResourcesByName(final String name) {
    return resourceRepo.findByNameContaining(name);
  }

  /** Searches for resources by name. */
  public List<Resource> listAll() {
    final var fileList = driveService.listFiles(10);
    log.info("Found file: {}", fileList);
    return new ArrayList<>();
  }

  /** Deletes a resource. */
  @Transactional
  public void deleteResource(final UUID id) {
    final Resource resource = getResource(id);
    // Delete the file from Google Drive first
    driveService.deleteFile(resource.getDriveFileId());
    // Remove any mentor profile picture that references this resource to satisfy FK constraints
    profilePicRepo.deleteByResourceId(id);
    // Now delete the resource record itself
    resourceRepo.deleteById(id);
  }

  /** Uploads a mentor's profile picture. */
  @Transactional
  public MentorProfilePicture uploadMentorProfilePicture(
      final String mentorEmail, final MultipartFile file) {

    final Optional<MentorProfilePicture> existingPicture =
        profilePicRepo.findByMentorEmail(mentorEmail);

    if (existingPicture.isPresent()) {
      deleteResource(existingPicture.get().getResourceId());
      profilePicRepo.deleteByMentorEmail(mentorEmail);
    }

    final Resource resource =
        uploadResource(
            file,
            "Profile picture for " + mentorEmail,
            "Profile picture for mentor with email " + mentorEmail,
            ResourceType.PROFILE_PICTURE);

    final MentorProfilePicture profilePicture =
        MentorProfilePicture.builder()
            .mentorEmail(mentorEmail)
            .resourceId(resource.getId())
            .resource(resource)
            .build();

    return profilePicRepo.create(profilePicture);
  }

  /** Gets a mentor's profile picture. */
  public MentorProfilePicture getMentorProfilePicture(final String mentorEmail) {
    return profilePicRepo
        .findByMentorEmail(mentorEmail)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Profile picture not found for mentor: " + mentorEmail));
  }

  /** Deletes a mentor's profile picture. */
  @Transactional
  public void deleteMentorProfilePicture(final String mentorEmail) {
    final MentorProfilePicture profilePicture = getMentorProfilePicture(mentorEmail);
    deleteResource(profilePicture.getResourceId());
    profilePicRepo.deleteByMentorEmail(mentorEmail);
  }
}
