package com.wcc.platform.domain.resource;

import com.wcc.platform.domain.platform.type.ResourceType;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Entity representing a resource stored in Google Drive. */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Resource {
  private UUID id;
  private String name;
  private String description;
  private String fileName;
  private String contentType;
  private long size;
  private String driveFileId;
  private String driveFileLink;
  private ResourceType resourceType;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}
