package com.wcc.platform.domain.resource;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Entity representing a mentor's profile picture. */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Data
@Builder(toBuilder = true)
public class MentorProfilePicture {
  private UUID id;
  private String mentorEmail;
  private UUID resourceId;
  private Resource resource;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}
