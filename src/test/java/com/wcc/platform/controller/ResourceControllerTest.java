package com.wcc.platform.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.configuration.TestConfig;
import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MemberProfilePicture;
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
@Import({SecurityConfig.class, TestConfig.class})
@WebMvcTest(ResourceController.class)
class ResourceControllerTest {

  private static final String CONTENT_TYPE = "image/jpeg";
  private Long memberId;
  @Autowired private MockMvc mockMvc;
  @MockBean private ResourceService resourceService;
  private UUID resourceId;
  private Resource resource;
  private MemberProfilePicture profilePicture;
  private MockMultipartFile multipartFile;

  @BeforeEach
  void setUp() {
    resourceId = UUID.randomUUID();
    memberId = 42L;

    resource =
        Resource.builder()
            .id(resourceId)
            .name("Test Resource")
            .description("Test Description")
            .fileName("test.jpg")
            .contentType(CONTENT_TYPE)
            .size(1024L)
            .driveFileId("drive-file-id")
            .driveFileLink("https://drive.google.com/file/d/drive-file-id/view")
            .resourceType(ResourceType.EVENT_IMAGE)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    profilePicture =
        MemberProfilePicture.builder()
            .memberId(memberId)
            .resourceId(resourceId)
            .resource(resource)
            .build();

    multipartFile =
        new MockMultipartFile("file", "test.jpg", CONTENT_TYPE, "test image content".getBytes());
  }

  @Test
  void uploadResourceShouldReturnCreatedResource() throws Exception {
    when(resourceService.uploadResource(
            any(MultipartFile.class),
            eq("Test Resource"),
            eq("Test Description"),
            eq(ResourceType.EVENT_IMAGE)))
        .thenReturn(resource);

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
        .andExpect(jsonPath("$.contentType").value(CONTENT_TYPE))
        .andExpect(jsonPath("$.size").value(1024))
        .andExpect(jsonPath("$.driveFileId").value("drive-file-id"))
        .andExpect(
            jsonPath("$.driveFileLink").value("https://drive.google.com/file/d/drive-file-id/view"))
        .andExpect(jsonPath("$.resourceType").value(ResourceType.EVENT_IMAGE.name()));
  }

  @Test
  void testResourceShouldReturnResource() throws Exception {
    when(resourceService.getResource(resourceId)).thenReturn(resource);

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
    List<Resource> resources = Collections.singletonList(resource);
    when(resourceService.getResourcesByType(ResourceType.EVENT_IMAGE)).thenReturn(resources);

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
    List<Resource> resources = Collections.singletonList(resource);
    when(resourceService.searchResourcesByName("Test")).thenReturn(resources);

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
    doNothing().when(resourceService).deleteResource(resourceId);

    mockMvc
        .perform(
            delete("/api/platform/v1/resources/{id}", resourceId)
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  void uploadMemberProfilePictureShouldReturnCreatedProfilePicture() throws Exception {
    when(resourceService.uploadMentorProfilePicture(eq(memberId), any(MultipartFile.class)))
        .thenReturn(profilePicture);

    mockMvc
        .perform(
            multipart("/api/platform/v1/resources/member-profile-picture")
                .file(multipartFile)
                .param("memberId", memberId.toString())
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.memberId").value(memberId))
        .andExpect(jsonPath("$.resourceId").value(resourceId.toString()));
  }

  @Test
  void testGetMentorProfilePictureShouldReturnProfilePicture() throws Exception {
    when(resourceService.getMemberProfilePicture(memberId)).thenReturn(profilePicture);

    mockMvc
        .perform(
            get("/api/platform/v1/resources/member-profile-picture/{memberId}", memberId)
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.memberId").value(memberId))
        .andExpect(jsonPath("$.resourceId").value(resourceId.toString()));
  }

  @Test
  void deleteMentorProfilePictureShouldReturnNoContent() throws Exception {
    doNothing().when(resourceService).deleteMemberProfilePicture(memberId);

    mockMvc
        .perform(
            delete("/api/platform/v1/resources/member-profile-picture/{memberId}", memberId)
                .header("X-API-KEY", "test-api-key")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }
}
