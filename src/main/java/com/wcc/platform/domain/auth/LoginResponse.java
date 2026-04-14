package com.wcc.platform.domain.auth;

import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.type.RoleType;
import java.util.List;

/**
 * Response returned after a successful or unsuccessful login attempt.
 *
 * <p>Encapsulates the authentication token, expiry, user roles, associated member details, and an
 * optional message for error or informational cases.
 */
public record LoginResponse(
    String token, String expiresAt, List<RoleType> roles, MemberDto member, String message) {

  /** Constructs an error response with a message and no authentication details. */
  public LoginResponse(final String message) {
    this(null, null, List.of(), null, message);
  }

  /** Constructs a response with roles and member info but no token (e.g. for /me endpoint). */
  public LoginResponse(final List<RoleType> roles, final MemberDto member) {
    this(null, null, roles, member, null);
  }

  /** Constructs a full authentication response with token and expiry. */
  public LoginResponse(
      final String token,
      final String expiresAt,
      final List<RoleType> roles,
      final MemberDto member) {
    this(token, expiresAt, roles, member, null);
  }
}
