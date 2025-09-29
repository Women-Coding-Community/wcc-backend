package com.wcc.platform.repository.postgres;

import static com.wcc.platform.repository.postgres.PostgresMemberRepository.MEMBER_ID_COLUMN;
import static com.wcc.platform.repository.postgres.PostgresResourceRepository.RESOURCE_ID_COLUMN;

import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MemberProfilePicture;
import com.wcc.platform.domain.resource.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

/** RowMapper for mapping database rows to MemberProfilePicture objects. */
public class MemberProfilePictureRowMapper implements RowMapper<MemberProfilePicture> {

  @Override
  public MemberProfilePicture mapRow(final ResultSet result, final int rowNum) throws SQLException {
    final String resourceIdStr = result.getString(RESOURCE_ID_COLUMN);

    final UUID resourceUuid;
    try {
      resourceUuid = UUID.fromString(resourceIdStr);
    } catch (IllegalArgumentException e) {
      throw new SQLException("Invalid resource_id UUID: " + resourceIdStr, e);
    }

    final Resource resource =
        Resource.builder()
            .id(resourceUuid)
            .name(result.getString("name"))
            .description(result.getString("description"))
            .fileName(result.getString("file_name"))
            .contentType(result.getString("content_type"))
            .size(result.getLong("size"))
            .driveFileId(result.getString("drive_file_id"))
            .driveFileLink(result.getString("drive_file_link"))
            .resourceType(ResourceType.fromId(result.getInt("resource_type_id")))
            .createdAt(result.getObject("created_at", OffsetDateTime.class))
            .updatedAt(result.getObject("updated_at", OffsetDateTime.class))
            .build();

    return MemberProfilePicture.builder()
        .memberId(result.getLong(MEMBER_ID_COLUMN))
        .resourceId(resourceUuid)
        .resource(resource)
        .build();
  }
}
