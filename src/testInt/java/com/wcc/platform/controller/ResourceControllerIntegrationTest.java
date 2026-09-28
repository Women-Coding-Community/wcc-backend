package com.wcc.platform.controller;

import static com.wcc.platform.domain.platform.type.RoleType.ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.wcc.platform.config.TestGoogleDriveRepositoryConfig;
import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.repository.ResourceRepository;
import com.wcc.platform.repository.googledrive.GoogleDriveTestUtils;
import com.wcc.platform.repository.postgres.DefaultDatabaseSetup;
import com.wcc.platform.service.AuthService;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Import(TestGoogleDriveRepositoryConfig.class)
class ResourceControllerIntegrationTest extends DefaultDatabaseSetup {

  private static final String DRIVE_FILE_ID = "test-drive-id";
  private static final String DRIVE_LINK = "https://drive.google.com/file/d/test-drive-id/view";

  @Autowired private MockMvc mockMvc;
  @Autowired private Drive mockDriveService;
  @Autowired private ResourceRepository resourceRepository;
  @MockBean private AuthService authService;

  @Mock private Drive.Files mockFiles;
  @Mock private Drive.Files.Create mockCreate;
  @Mock private Drive.Permissions mockPermissions;
  @Mock private Drive.Permissions.Create mockPermissionCreate;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(mockDriveService.files()).thenReturn(mockFiles);
    when(mockDriveService.permissions()).thenReturn(mockPermissions);

    // Mock Authentication for AOP security checks
    var userAccount = new UserAccount(1L, "admin@test.com", ADMIN);
    var member = Member.builder().id(1L).email("admin@test.com").build();
    var user = new UserAccount.User(userAccount, member);
    var auth = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);

    doNothing().when(authService).requireRole(any());
  }

  @Test
  @DisplayName(
      "Given valid multipart file, when uploading resource, then return 201 and created resource")
  void shouldUploadResourceSuccessfully() throws Exception {
    // Given
    File driveFile = GoogleDriveTestUtils.createMockFile(DRIVE_FILE_ID, "test.pdf", DRIVE_LINK);

    when(mockFiles.create(any(File.class), any())).thenReturn(mockCreate);
    when(mockCreate.setSupportsAllDrives(true)).thenReturn(mockCreate);
    when(mockCreate.setFields(any(String.class))).thenReturn(mockCreate);
    when(mockCreate.execute()).thenReturn(driveFile);

    when(mockPermissions.create(eq(DRIVE_FILE_ID), any(Permission.class)))
        .thenReturn(mockPermissionCreate);
    when(mockPermissionCreate.setSupportsAllDrives(true)).thenReturn(mockPermissionCreate);
    when(mockPermissionCreate.execute()).thenReturn(new Permission());

    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "test content".getBytes());

    // When
    var result =
        mockMvc.perform(
            multipart("/api/platform/v1/resources")
                .file(multipartFile)
                .param("name", "Integrated Resource")
                .param("description", "Integrated Description")
                .param("resourceType", ResourceType.EVENT_IMAGE.name())
                .header("X-API-KEY", "test-api-key"));

    // Then
    result
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Integrated Resource"))
        .andExpect(jsonPath("$.driveFileId").value(DRIVE_FILE_ID))
        .andExpect(jsonPath("$.driveFileLink").value(DRIVE_LINK))
        .andExpect(jsonPath("$.resourceType").value(ResourceType.EVENT_IMAGE.name()));

    String idString =
        result.andReturn().getResponse().getContentAsString().split("\"id\":\"")[1].split("\"")[0];
    UUID resourceId = UUID.fromString(idString);
    var savedResource = resourceRepository.findById(resourceId);
    assertThat(savedResource).isPresent();
    assertThat(savedResource.get().getName()).isEqualTo("Integrated Resource");
  }
}
