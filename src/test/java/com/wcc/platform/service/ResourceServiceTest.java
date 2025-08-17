package com.wcc.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.services.drive.model.File;
import com.wcc.platform.configuration.GoogleDriveFoldersProperties;
import com.wcc.platform.domain.exceptions.ResourceNotFoundException;
import com.wcc.platform.domain.resource.MentorProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.domain.resource.ResourceType;
import com.wcc.platform.repository.MentorProfilePictureRepository;
import com.wcc.platform.repository.ResourceRepository;
import com.wcc.platform.repository.googledrive.GoogleDriveService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

  @Mock private ResourceRepository resourceRepository;

  @Mock private MentorProfilePictureRepository repository;

  @Mock private GoogleDriveService googleDriveService;

  @Mock private GoogleDriveFoldersProperties driveFolders;

  @InjectMocks private ResourceService resourceService;

  private UUID resourceId;
  private Resource resource;
  private MentorProfilePicture profilePicture;
  private MultipartFile multipartFile;
  private File driveFile;

  @BeforeEach
  void setUp() {
    // Default to blank folders so the service uses the root upload overload (backward compatible)
    org.mockito.Mockito.lenient().when(driveFolders.getResources()).thenReturn("");
    org.mockito.Mockito.lenient().when(driveFolders.getMentorPhoto()).thenReturn("");
    resourceId = UUID.randomUUID();

    resource =
        Resource.builder()
            .id(resourceId)
            .name("Test Resource")
            .description("Test Description")
            .fileName("test.jpg")
            .contentType("image/jpeg")
            .size(1024L)
            .driveFileId("drive-file-id")
            .driveFileLink("https://drive.google.com/file/d/drive-file-id/view")
            .resourceType(ResourceType.IMAGE)
            .build();

    profilePicture =
        MentorProfilePicture.builder()
            .id(UUID.randomUUID())
            .mentorEmail("test@example.com")
            .resourceId(resourceId)
            .resource(resource)
            .build();

    multipartFile =
        new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

    driveFile = new File();
    driveFile.setId("drive-file-id");
    driveFile.setName("test.jpg");
    driveFile.setWebViewLink("https://drive.google.com/file/d/drive-file-id/view");
  }

  @Test
  void uploadResourceShouldReturnCreatedResource() {
    // Arrange
    when(googleDriveService.uploadFile(any(MultipartFile.class))).thenReturn(driveFile);
    when(resourceRepository.create(any(Resource.class))).thenReturn(resource);

    // Act
    Resource result =
        resourceService.uploadResource(
            multipartFile, "Test Resource", "Test Description", ResourceType.IMAGE);

    // Assert
    assertNotNull(result);
    assertEquals(resourceId, result.getId());
    assertEquals("Test Resource", result.getName());
    assertEquals("Test Description", result.getDescription());
    assertEquals("test.jpg", result.getFileName());
    assertEquals("image/jpeg", result.getContentType());
    assertEquals(1024L, result.getSize());
    assertEquals("drive-file-id", result.getDriveFileId());
    assertEquals("https://drive.google.com/file/d/drive-file-id/view", result.getDriveFileLink());
    assertEquals(ResourceType.IMAGE, result.getResourceType());

    verify(googleDriveService, times(1)).uploadFile(any(MultipartFile.class));
    verify(resourceRepository, times(1)).create(any(Resource.class));
  }

  @Test
  void testGetResourceShouldReturnResourceWhenResourceExists() {
    // Arrange
    when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

    // Act
    Resource result = resourceService.getResource(resourceId);

    // Assert
    assertNotNull(result);
    assertEquals(resourceId, result.getId());
    assertEquals("Test Resource", result.getName());

    verify(resourceRepository, times(1)).findById(resourceId);
  }

  @Test
  void testGetResourceShouldThrowResourceNotFoundExceptionWhenResourceDoesNotExist() {
    // Arrange
    when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> resourceService.getResource(resourceId));

    verify(resourceRepository, times(1)).findById(resourceId);
  }

  @Test
  void testGetResourcesByTypeShouldReturnResourceList() {
    // Arrange
    List<Resource> resources = Collections.singletonList(resource);
    when(resourceRepository.findByType(ResourceType.IMAGE)).thenReturn(resources);

    // Act
    List<Resource> result = resourceService.getResourcesByType(ResourceType.IMAGE);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(resourceId, result.getFirst().getId());

    verify(resourceRepository, times(1)).findByType(ResourceType.IMAGE);
  }

  @Test
  void searchResourcesByNameShouldReturnResourceList() {
    // Arrange
    List<Resource> resources = Collections.singletonList(resource);
    when(resourceRepository.findByNameContaining("Test")).thenReturn(resources);

    // Act
    List<Resource> result = resourceService.searchResourcesByName("Test");

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(resourceId, result.getFirst().getId());

    verify(resourceRepository, times(1)).findByNameContaining("Test");
  }

  @Test
  void deleteResourceShouldDeleteResource() {
    // Arrange
    when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
    doNothing().when(googleDriveService).deleteFile(anyString());
    doNothing().when(resourceRepository).deleteById(any(UUID.class));

    // Act
    resourceService.deleteResource(resourceId);

    // Assert
    verify(resourceRepository, times(1)).findById(resourceId);
    verify(googleDriveService, times(1)).deleteFile("drive-file-id");
    // Ensure dependent mentor_profile_picture entries are deleted first
    verify(repository, times(1)).deleteByResourceId(resourceId);
    verify(resourceRepository, times(1)).deleteById(resourceId);
  }

  @Test
  void uploadProfilePictureShouldReturnCreatedProfilePictureWhenMentorDoesNotHaveProfilePicture() {
    // Arrange
    when(repository.findByMentorEmail("test@example.com")).thenReturn(Optional.empty());
    when(googleDriveService.uploadFile(any(MultipartFile.class))).thenReturn(driveFile);
    when(resourceRepository.create(any(Resource.class))).thenReturn(resource);
    when(repository.create(any(MentorProfilePicture.class))).thenReturn(profilePicture);

    // Act
    var result = resourceService.uploadMentorProfilePicture("test@example.com", multipartFile);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getMentorEmail());
    assertEquals(resourceId, result.getResourceId());

    verify(repository, times(1)).findByMentorEmail("test@example.com");
    verify(googleDriveService, times(1)).uploadFile(any(MultipartFile.class));
    verify(resourceRepository, times(1)).create(any(Resource.class));
    verify(repository, times(1)).create(any(MentorProfilePicture.class));
  }

  @Test
  void testGetMentorProfileShouldReturnProfilePictureWhenProfilePictureExists() {
    // Arrange
    when(repository.findByMentorEmail("test@example.com")).thenReturn(Optional.of(profilePicture));

    // Act
    MentorProfilePicture result = resourceService.getMentorProfilePicture("test@example.com");

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getMentorEmail());
    assertEquals(resourceId, result.getResourceId());

    verify(repository, times(1)).findByMentorEmail("test@example.com");
  }

  @Test
  void testGetMentorProfileShouldThrowExceptionWhenProfilePictureDoesNotExist() {
    // Arrange
    when(repository.findByMentorEmail("test@example.com")).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class,
        () -> resourceService.getMentorProfilePicture("test@example.com"));

    verify(repository, times(1)).findByMentorEmail("test@example.com");
  }

  @Test
  void testDeleteMentorProfilePictureShouldDeleteProfilePicture() {
    // Arrange
    when(repository.findByMentorEmail("test@example.com")).thenReturn(Optional.of(profilePicture));
    when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
    doNothing().when(googleDriveService).deleteFile(anyString());
    doNothing().when(resourceRepository).deleteById(any(UUID.class));
    doNothing().when(repository).deleteByMentorEmail(anyString());

    // Act
    resourceService.deleteMentorProfilePicture("test@example.com");

    // Assert
    verify(repository, times(1)).findByMentorEmail("test@example.com");
    verify(resourceRepository, times(1)).findById(resourceId);
    verify(googleDriveService, times(1)).deleteFile("drive-file-id");
    // Ensure dependent mentor_profile_picture entries are deleted via deleteResource path
    verify(repository, times(1)).deleteByResourceId(resourceId);
    verify(resourceRepository, times(1)).deleteById(resourceId);
    verify(repository, times(1)).deleteByMentorEmail("test@example.com");
  }

  @Test
  void uploadMentorProfilePictureShouldReplaceExistingPicture() {
    // Arrange: existing picture present
    when(repository.findByMentorEmail("test@example.com")).thenReturn(Optional.of(profilePicture));
    when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
    doNothing().when(googleDriveService).deleteFile(anyString());
    doNothing().when(resourceRepository).deleteById(any(UUID.class));
    // After deleting old, proceed to upload new
    when(googleDriveService.uploadFile(any(MultipartFile.class))).thenReturn(driveFile);
    when(resourceRepository.create(any(Resource.class))).thenReturn(resource);
    when(repository.create(any(MentorProfilePicture.class))).thenReturn(profilePicture);

    // Act
    var result = resourceService.uploadMentorProfilePicture("test@example.com", multipartFile);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getMentorEmail());
    assertEquals(resourceId, result.getResourceId());

    // Ensure old resource and linkage are removed
    verify(repository, times(1)).findByMentorEmail("test@example.com");
    verify(resourceRepository, times(1)).findById(resourceId);
    verify(googleDriveService, times(1)).deleteFile("drive-file-id");
    verify(repository, times(1)).deleteByResourceId(resourceId);
    verify(repository, times(1)).deleteByMentorEmail("test@example.com");

    // Ensure new resource and profile picture are created
    verify(googleDriveService, times(1)).uploadFile(any(MultipartFile.class));
    verify(resourceRepository, times(1)).create(any(Resource.class));
    verify(repository, times(1)).create(any(MentorProfilePicture.class));
  }
}
