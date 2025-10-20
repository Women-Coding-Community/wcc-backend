package com.wcc.platform.domain.auth;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Opaque token stored server-side with TTL. */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserToken {
  private String token;
  private Integer userId;
  private OffsetDateTime issuedAt;
  private OffsetDateTime expiresAt;
  private boolean revoked;
}
