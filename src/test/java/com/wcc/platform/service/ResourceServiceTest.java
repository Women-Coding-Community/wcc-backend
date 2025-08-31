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

import com.wcc.platform.domain.exceptions.ResourceNotFoundException;
import com.wcc.platform.domain.platform.filestorage.FileStored;
import com.wcc.platform.domain.platform.type.ContentType;
import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MentorProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.properties.FolderStorageProperties;
import com.wcc.platform.repository.FileStorageRepository;
import com.wcc.platform.repository.MentorProfilePictureRepository;
import com.wcc.platform.repository.ResourceRepository;
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

  @Mock private FileStorageRepository fileStorageRepository;

  @InjectMocks private ResourceService resourceService;

  private UUID resourceId;
  private Resource resource;
  private MentorProfilePicture profilePicture;
  private MultipartFile multipartFile;
  private FileStored fileStored;

  @BeforeEach
  void setUp() {
    // Setup folders mapping
    var folders = new FolderStorageProperties();
    folders.setImagesFolder("images-folder-id");
    folders.setMentorsProfileFolder("mentors-profile-folder-id");
    folders.setResourcesFolder("resources-folder-id");
    folders.setEventsFolder("events-folder-id");
    folders.setMentorsFolder("mentors-folder-id");

    org.mockito.Mockito.lenient().when(fileStorageRepository.getFolders()).thenReturn(folders);

    resourceId = UUID.randomUUID();

    resource =
        Resource.builder()
            .id(resourceId)
            .name("Test Resource")
            .description("Test Description")
            .fileName("test.jpg")
            .contentType(ContentType.IMAGE)
            .size(1024L)
            .driveFileId("drive-file-id")
            .driveFileLink("https://drive.google.com/file/d/drive-file-id/view")
            .resourceType(ResourceType.EVENT_IMAGE)
            .build();

    profilePicture =
        MentorProfilePicture.builder()
            .id(UUID.randomUUID())
            .mentorEmail("test@example.com")
            .resourceId(resourceId)
            .resource(resource)
            .build();

    multipartFile = new MockMultipartFile("file", "test.jpg", "", "test image content".getBytes());

    fileStored =
        new FileStored("drive-file-id", "https://drive.google.com/file/d/drive-file-id/view");
  }

  @Test
  void uploadResourceShouldReturnCreatedResource() {
    // Arrange
    when(fileStorageRepository.uploadFile(any(MultipartFile.class), anyString()))
        .thenReturn(fileStored);
    when(resourceRepository.create(any(Resource.class))).thenReturn(resource);

    // Act
    Resource result =
        resourceService.uploadResource(
            multipartFile, "Test Resource", "Test Description", ResourceType.EVENT_IMAGE);

    // Assert
    assertNotNull(result);
    assertEquals(resourceId, result.getId());
    assertEquals("Test Resource", result.getName());
    assertEquals("Test Description", result.getDescription());
    assertEquals("test.jpg", result.getFileName());
    assertEquals(ContentType.IMAGE, result.getContentType());
    assertEquals(1024L, result.getSize());
    assertEquals("drive-file-id", result.getDriveFileId());
    assertEquals("https://drive.google.com/file/d/drive-file-id/view", result.getDriveFileLink());
    assertEquals(ResourceType.EVENT_IMAGE, result.getResourceType());

    verify(fileStorageRepository, times(1)).uploadFile(any(MultipartFile.class), anyString());
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
    when(resourceRepository.findByType(ResourceType.EVENT_IMAGE)).thenReturn(resources);

    // Act
    List<Resource> result = resourceService.getResourcesByType(ResourceType.EVENT_IMAGE);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(resourceId, result.getFirst().getId());

    verify(resourceRepository, times(1)).findByType(ResourceType.EVENT_IMAGE);
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
    doNothing().when(fileStorageRepository).deleteFile(anyString());
    doNothing().when(resourceRepository).deleteById(any(UUID.class));

    // Act
    resourceService.deleteResource(resourceId);

    // Assert
    verify(resourceRepository, times(1)).findById(resourceId);
    verify(fileStorageRepository, times(1)).deleteFile("drive-file-id");
    // Ensure dependent mentor_profile_picture entries are deleted first
    verify(repository, times(1)).deleteByResourceId(resourceId);
    verify(resourceRepository, times(1)).deleteById(resourceId);
  }

  @Test
  void uploadProfilePictureShouldReturnCreatedProfilePictureWhenMentorDoesNotHaveProfilePicture() {
    // Arrange
    when(repository.findByMentorEmail("test@example.com")).thenReturn(Optional.empty());
    when(fileStorageRepository.uploadFile(any(MultipartFile.class), anyString()))
        .thenReturn(fileStored);
    when(resourceRepository.create(any(Resource.class))).thenReturn(resource);
    when(repository.create(any(MentorProfilePicture.class))).thenReturn(profilePicture);

    // Act
    var result = resourceService.uploadMentorProfilePicture("test@example.com", multipartFile);

    // Assert
    assertNotNull(result);
    assertEquals("test@example.com", result.getMentorEmail());
    assertEquals(resourceId, result.getResourceId());

    verify(repository, times(1)).findByMentorEmail("test@example.com");
    verify(fileStorageRepository, times(1)).uploadFile(any(MultipartFile.class), anyString());
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
    doNothing().when(fileStorageRepository).deleteFile(anyString());
    doNothing().when(resourceRepository).deleteById(any(UUID.class));
    doNothing().when(repository).deleteByMentorEmail(anyString());

    // Act
    resourceService.deleteMentorProfilePicture("test@example.com");

    // Assert
    verify(repository, times(1)).findByMentorEmail("test@example.com");
    verify(resourceRepository, times(1)).findById(resourceId);
    verify(fileStorageRepository, times(1)).deleteFile("drive-file-id");
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
    doNothing().when(fileStorageRepository).deleteFile(anyString());
    doNothing().when(resourceRepository).deleteById(any(UUID.class));
    // After deleting old, proceed to upload new
    when(fileStorageRepository.uploadFile(any(MultipartFile.class), anyString()))
        .thenReturn(fileStored);
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
    verify(fileStorageRepository, times(1)).deleteFile("drive-file-id");
    verify(repository, times(1)).deleteByResourceId(resourceId);
    verify(repository, times(1)).deleteByMentorEmail("test@example.com");

    // Ensure new resource and profile picture are created
    verify(fileStorageRepository, times(1)).uploadFile(any(MultipartFile.class), anyString());
    verify(resourceRepository, times(1)).create(any(Resource.class));
    verify(repository, times(1)).create(any(MentorProfilePicture.class));
  }
}
