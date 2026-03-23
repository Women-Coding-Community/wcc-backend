package com.wcc.platform.domain.auth;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents a single-use token issued to allow a user to reset their password. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

  private String token;
  private Integer userId;
  private OffsetDateTime issuedAt;
  private OffsetDateTime expiresAt;
  private boolean used;
}
