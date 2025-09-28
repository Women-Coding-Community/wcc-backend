package com.wcc.platform.domain.resource;

import jakarta.validation.constraints.NotNull;
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
public class MemberProfilePicture {
  @NotNull private Integer memberId;
  @NotNull private UUID resourceId;
  private Resource resource;
}
