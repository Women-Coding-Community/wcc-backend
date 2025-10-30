package com.wcc.platform.domain.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class UserTokenTest {
  private final UserToken token =
      UserToken.builder().token("token").userId(2).revoked(false).build();

  @Test
  void testToString() {
    assertEquals(
        "UserToken(token=token, userId=2, issuedAt=null, expiresAt=null, revoked=false)",
        token.toString());
  }

  @Test
  void testEquals() {

    assertNotEquals(new UserToken(), token);
  }
}
