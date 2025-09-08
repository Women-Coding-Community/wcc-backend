package com.wcc.platform.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MentorProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import com.wcc.platform.service.ResourceService;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(ResourceController.class)
class ResourceControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ResourceService resourceService;

  private UUID resourceId;
  private Resource resource;
  private MentorProfilePicture profilePicture;
  private MockMultipartFile multipartFile;

  @BeforeEach
  void setUp() {
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
            .resourceType(ResourceType.EVENT_IMAGE)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    profilePicture =
        MentorProfilePicture.builder()
            .id(UUID.randomUUID())
            .mentorEmail("test@example.com")
            .resourceId(resourceId)
            .resource(resource)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    multipartFile =
        new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());
  }

  @Test
  void uploadResourceShouldReturnCreatedResource() throws Exception {
    // Arrange
    when(resourceService.uploadResource(
            any(MultipartFile.class),
            eq("Test Resource"),
            eq("Test Description"),
            eq(ResourceType.EVENT_IMAGE)))
        .thenReturn(resource);

    // Act & Assert
    mockMvc
        .perform(
            multipart("/api/platform/v1/resources")
                .file(multipartFile)
                .param("name", "Test Resource")
                .param("description", "Test Description")
                .param("resourceType", ResourceType.EVENT_IMAGE.name())
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(resourceId.toString()))
        .andExpect(jsonPath("$.name").value("Test Resource"))
        .andExpect(jsonPath("$.description").value("Test Description"))
        .andExpect(jsonPath("$.fileName").value("test.jpg"))
        .andExpect(jsonPath("$.contentType").value("image/jpeg"))
        .andExpect(jsonPath("$.size").value(1024))
        .andExpect(jsonPath("$.driveFileId").value("drive-file-id"))
        .andExpect(
            jsonPath("$.driveFileLink").value("https://drive.google.com/file/d/drive-file-id/view"))
        .andExpect(jsonPath("$.resourceType").value(ResourceType.EVENT_IMAGE.name()));
  }

  @Test
  void testResourceShouldReturnResource() throws Exception {
    // Arrange
    when(resourceService.getResource(resourceId)).thenReturn(resource);

    // Act & Assert
    mockMvc
        .perform(
            get("/api/platform/v1/resources/{id}", resourceId)
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(resourceId.toString()))
        .andExpect(jsonPath("$.name").value("Test Resource"));
  }

  @Test
  void testResourcesByTypeShouldReturnResourceList() throws Exception {
    // Arrange
    List<Resource> resources = Collections.singletonList(resource);
    when(resourceService.getResourcesByType(ResourceType.EVENT_IMAGE)).thenReturn(resources);

    // Act & Assert
    mockMvc
        .perform(
            get("/api/platform/v1/resources")
                .param("resourceType", ResourceType.EVENT_IMAGE.name())
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(resourceId.toString()))
        .andExpect(jsonPath("$[0].name").value("Test Resource"));
  }

  @Test
  void searchResourcesByNameShouldReturnResourceList() throws Exception {
    // Arrange
    List<Resource> resources = Collections.singletonList(resource);
    when(resourceService.searchResourcesByName("Test")).thenReturn(resources);

    // Act & Assert
    mockMvc
        .perform(
            get("/api/platform/v1/resources/search")
                .param("name", "Test")
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(resourceId.toString()))
        .andExpect(jsonPath("$[0].name").value("Test Resource"));
  }

  @Test
  void deleteResourceShouldReturnNoContent() throws Exception {
    // Arrange
    doNothing().when(resourceService).deleteResource(resourceId);

    // Act & Assert
    mockMvc
        .perform(
            delete("/api/platform/v1/resources/{id}", resourceId)
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void uploadMentorProfilePictureShouldReturnCreatedProfilePicture() throws Exception {
    // Arrange
    when(resourceService.uploadMentorProfilePicture(
            eq("test@example.com"), any(MultipartFile.class)))
        .thenReturn(profilePicture);

    // Act & Assert
    mockMvc
        .perform(
            multipart("/api/platform/v1/resources/mentor-profile-picture")
                .file(multipartFile)
                .param("mentorEmail", "test@example.com")
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.mentorEmail").value("test@example.com"))
        .andExpect(jsonPath("$.resourceId").value(resourceId.toString()));
  }

  @Test
  void testGetMentorProfilePictureShouldReturnProfilePicture() throws Exception {
    // Arrange
    when(resourceService.getMentorProfilePicture("test@example.com")).thenReturn(profilePicture);

    // Act & Assert
    mockMvc
        .perform(
            get(
                    "/api/platform/v1/resources/mentor-profile-picture/{mentorEmail}",
                    "test@example.com")
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.mentorEmail").value("test@example.com"))
        .andExpect(jsonPath("$.resourceId").value(resourceId.toString()));
  }

  @Test
  void deleteMentorProfilePictureShouldReturnNoContent() throws Exception {
    // Arrange
    doNothing().when(resourceService).deleteMentorProfilePicture(anyString());

    // Act & Assert
    mockMvc
        .perform(
            delete(
                    "/api/platform/v1/resources/mentor-profile-picture/{mentorEmail}",
                    "test@example.com")
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }
}
