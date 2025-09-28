package com.wcc.platform.domain.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class MemberProfilePictureTest {

  @Test
  void shouldBuildMentorProfilePictureWithAllFields() {
    var memberId = 10;
    UUID resourceId = UUID.randomUUID();
    Resource resource = new Resource();

    MemberProfilePicture profilePicture =
        MemberProfilePicture.builder()
            .memberId(memberId)
            .resourceId(resourceId)
            .resource(resource)
            .build();

    assertEquals(memberId, profilePicture.getMemberId());
    assertEquals(resourceId, profilePicture.getResourceId());
    assertEquals(resource, profilePicture.getResource());
  }

  @Test
  void shouldUpdateMentorProfilePictureFields() {
    MemberProfilePicture profilePicture = new MemberProfilePicture();
    var newId = 14;
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
    var memberId = 20;
    var profilePicture1 = MemberProfilePicture.builder().memberId(memberId).build();
    var profilePicture2 = MemberProfilePicture.builder().memberId(memberId).build();

    assertEquals(profilePicture1, profilePicture2);
    assertEquals(profilePicture1.hashCode(), profilePicture2.hashCode());
  }

  @Test
  void shouldGenerateDifferentHashCodesForDifferentMentorProfilePictures() {
    var profilePicture1 = MemberProfilePicture.builder().memberId(10).build();
    var profilePicture2 = MemberProfilePicture.builder().memberId(20).build();

    assertNotEquals(profilePicture1.hashCode(), profilePicture2.hashCode());
  }
}
