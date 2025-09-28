package com.wcc.platform.service;

import com.wcc.platform.domain.exceptions.ResourceNotFoundException;
import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.repository.FileStorageRepository;
import com.wcc.platform.repository.MemberProfilePictureRepository;
import com.wcc.platform.repository.ResourceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/** Service for managing resources and profile pictures. */
@Slf4j
@Service
@AllArgsConstructor
public class ResourceService {

  private final ResourceRepository resourceRepo;
  private final MemberProfilePictureRepository profilePicRepo;
  private final FileStorageRepository fileStorageRepo;

  /** Uploads a resource to Google Drive and stores its metadata in the database. */
  @Transactional
  public Resource uploadResource(
      final MultipartFile file,
      final String name,
      final String description,
      final ResourceType resourceType) {
    return saveAndUploadResource(file, name, description, resourceType);
  }

  /** Deletes a mentor's profile picture. */
  @Transactional
  public void deleteMemberProfilePicture(final Integer memberId) {
    deleteProfileBy(memberId);
  }

  /** Deletes a resource. */
  @Transactional
  public void deleteResource(final UUID id) {
    deleteResourceBy(id);
  }

  /** Uploads a mentor's profile picture. */
  @Transactional
  public MemberProfilePicture uploadMentorProfilePicture(
      final Integer memberId, final MultipartFile file) {
    return uploadProfile(memberId, file);
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

  /** Gets a mentor's profile picture. */
  public MemberProfilePicture getMemberProfilePicture(final Integer memberId) {
    return profilePicRepo
        .findByMemberId(memberId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Profile picture not found for the member: " + memberId));
  }

  private MemberProfilePicture uploadProfile(final Integer memberId, final MultipartFile file) {
    final Optional<MemberProfilePicture> existingPicture = profilePicRepo.findByMemberId(memberId);

    if (existingPicture.isPresent()) {
      deleteResourceBy(existingPicture.get().getResourceId());
      profilePicRepo.deleteByMemberId(memberId);
    }

    final Resource resource =
        saveAndUploadResource(
            file, StringUtils.EMPTY, StringUtils.EMPTY, ResourceType.PROFILE_PICTURE);

    final MemberProfilePicture profilePicture =
        MemberProfilePicture.builder()
            .memberId(memberId)
            .resourceId(resource.getId())
            .resource(resource)
            .build();

    return profilePicRepo.create(profilePicture);
  }

  private void deleteResourceBy(final UUID resourceId) {
    final var resource = getResource(resourceId);
    profilePicRepo.deleteById(resourceId);
    resourceRepo.deleteById(resourceId);
    fileStorageRepo.deleteFile(resource.getDriveFileId());
  }

  private void deleteProfileBy(final Integer memberId) {
    final var profilePicture = getMemberProfilePicture(memberId);
    deleteResourceBy(profilePicture.getResourceId());
  }

  private Resource saveAndUploadResource(
      final MultipartFile file,
      final String name,
      final String description,
      final ResourceType resourceType) {

    final var folder = resourceType.toFolderId(fileStorageRepo.getFolders());
    final var fileStored = fileStorageRepo.uploadFile(file, folder);

    final Resource resource =
        Resource.builder()
            .id(UUID.randomUUID())
            .name(name)
            .description(description)
            .fileName(file.getOriginalFilename())
            .contentType(file.getContentType())
            .size(file.getSize())
            .driveFileId(fileStored.id())
            .driveFileLink(fileStored.webLink())
            .resourceType(resourceType)
            .build();

    return resourceRepo.create(resource);
  }
}
