package com.wcc.platform.repository.postgres;

import static com.wcc.platform.domain.platform.type.ResourceType.IMAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MemberProfilePictureRowMapperTest {
  private final MemberProfilePictureRowMapper rowMapper = new MemberProfilePictureRowMapper();

  @Test
  void mapRowShouldMapResultSetToMemberProfilePicture() throws SQLException {
    ResultSet resultSet = mock(ResultSet.class);
    UUID resourceId = UUID.randomUUID();
    Long memberId = 123L;
    String name = "Profile Picture";
    String description = "A sample profile picture";
    String fileName = "profile-pic.png";
    String contentType = "image/png";
    long size = 1024L;
    String driveFileId = "1234drive";
    String driveFileLink = "http://drive.com/file/1234drive";
    OffsetDateTime createdAt = OffsetDateTime.now();
    OffsetDateTime updatedAt = OffsetDateTime.now();

    when(resultSet.getString("resource_id")).thenReturn(resourceId.toString());
    when(resultSet.getLong("member_id")).thenReturn(memberId);
    when(resultSet.getString("name")).thenReturn(name);
    when(resultSet.getString("description")).thenReturn(description);
    when(resultSet.getString("file_name")).thenReturn(fileName);
    when(resultSet.getString("content_type")).thenReturn(contentType);
    when(resultSet.getLong("size")).thenReturn(size);
    when(resultSet.getString("drive_file_id")).thenReturn(driveFileId);
    when(resultSet.getString("drive_file_link")).thenReturn(driveFileLink);
    when(resultSet.getInt("resource_type_id")).thenReturn(IMAGE.getResourceTypeId());
    when(resultSet.getObject("created_at", OffsetDateTime.class)).thenReturn(createdAt);
    when(resultSet.getObject("updated_at", OffsetDateTime.class)).thenReturn(updatedAt);

    MemberProfilePicture result = rowMapper.mapRow(resultSet, 1);

    assertNotNull(result);
    assertEquals(memberId, result.getMemberId());
    assertEquals(resourceId, result.getResourceId());

    Resource resource = result.getResource();
    assertNotNull(resource);
    assertEquals(resourceId, resource.getId());
    assertEquals(name, resource.getName());
    assertEquals(description, resource.getDescription());
    assertEquals(fileName, resource.getFileName());
    assertEquals(contentType, resource.getContentType());
    assertEquals(size, resource.getSize());
    assertEquals(driveFileId, resource.getDriveFileId());
    assertEquals(driveFileLink, resource.getDriveFileLink());
    assertEquals(IMAGE, resource.getResourceType());
    assertEquals(createdAt, resource.getCreatedAt());
    assertEquals(updatedAt, resource.getUpdatedAt());
  }

  @Test
  void mapRowShouldThrowSqlExceptionWhenResourceIdIsInvalid() throws SQLException {
    var resultSet = mock(ResultSet.class);
    when(resultSet.getString("resource_id")).thenReturn("invalid-uuid");

    assertThrows(SQLException.class, () -> rowMapper.mapRow(resultSet, 1));
  }
}
