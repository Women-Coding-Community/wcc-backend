package com.wcc.platform.domain.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class MemberProfilePictureTest {
  private static final Long MEMBER_ID = 10L;

  @Test
  void shouldBuildMentorProfilePictureWithAllFields() {

    UUID resourceId = UUID.randomUUID();
    Resource resource = new Resource();

    MemberProfilePicture profilePicture =
        MemberProfilePicture.builder()
            .memberId(MEMBER_ID)
            .resourceId(resourceId)
            .resource(resource)
            .build();

    assertEquals(MEMBER_ID, profilePicture.getMemberId());
    assertEquals(resourceId, profilePicture.getResourceId());
    assertEquals(resource, profilePicture.getResource());
  }

  @Test
  void shouldUpdateMentorProfilePictureFields() {
    MemberProfilePicture profilePicture = new MemberProfilePicture();
    var newId = 14L;
    UUID newResourceId = UUID.randomUUID();
    Resource newResource = new Resource();

    profilePicture.setMemberId(newId);
    profilePicture.setResourceId(newResourceId);
    profilePicture.setResource(newResource);

    assertEquals(newId, profilePicture.getMemberId());
    assertEquals(newResourceId, profilePicture.getResourceId());
    assertEquals(newResource, profilePicture.getResource());
  }

  @Test
  void shouldHandleNullFieldsGracefully() {
    MemberProfilePicture profilePicture = new MemberProfilePicture();

    assertNull(profilePicture.getMemberId());
    assertNull(profilePicture.getResourceId());
    assertNull(profilePicture.getResource());
  }

  @Test
  void shouldCompareEqualMentorProfilePictures() {
    var memberId = 20L;
    var profilePicture1 = MemberProfilePicture.builder().memberId(memberId).build();
    var profilePicture2 = MemberProfilePicture.builder().memberId(memberId).build();

    assertEquals(profilePicture1, profilePicture2);
    assertEquals(profilePicture1.hashCode(), profilePicture2.hashCode());
  }

  @Test
  void shouldGenerateDifferentHashCodesForDifferentMentorProfilePictures() {
    var profilePicture1 = MemberProfilePicture.builder().memberId(10L).build();
    var profilePicture2 = MemberProfilePicture.builder().memberId(20L).build();

    assertNotEquals(profilePicture1.hashCode(), profilePicture2.hashCode());
  }
}
