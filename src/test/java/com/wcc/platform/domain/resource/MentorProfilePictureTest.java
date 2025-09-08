package com.wcc.platform.domain.resource;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MentorProfilePictureTest {

  @Test
  void shouldBuildMentorProfilePictureWithAllFields() {
    UUID uuid = UUID.randomUUID();
    String email = "mentor@example.com";
    UUID resourceId = UUID.randomUUID();
    Resource resource = new Resource();
    OffsetDateTime now = OffsetDateTime.now();

    MentorProfilePicture profilePicture =
        MentorProfilePicture.builder()
            .id(uuid)
            .mentorEmail(email)
            .resourceId(resourceId)
            .resource(resource)
            .createdAt(now)
            .updatedAt(now)
            .build();

    assertEquals(uuid, profilePicture.getId());
    assertEquals(email, profilePicture.getMentorEmail());
    assertEquals(resourceId, profilePicture.getResourceId());
    assertEquals(resource, profilePicture.getResource());
    assertEquals(now, profilePicture.getCreatedAt());
    assertEquals(now, profilePicture.getUpdatedAt());
  }

  @Test
  void shouldUpdateMentorProfilePictureFields() {
    MentorProfilePicture profilePicture = new MentorProfilePicture();
    UUID newId = UUID.randomUUID();
    String newEmail = "newmentor@example.com";
    UUID newResourceId = UUID.randomUUID();
    Resource newResource = new Resource();
    OffsetDateTime newTime = OffsetDateTime.now();

    profilePicture.setId(newId);
    profilePicture.setMentorEmail(newEmail);
    profilePicture.setResourceId(newResourceId);
    profilePicture.setResource(newResource);
    profilePicture.setCreatedAt(newTime);
    profilePicture.setUpdatedAt(newTime);

    assertEquals(newId, profilePicture.getId());
    assertEquals(newEmail, profilePicture.getMentorEmail());
    assertEquals(newResourceId, profilePicture.getResourceId());
    assertEquals(newResource, profilePicture.getResource());
    assertEquals(newTime, profilePicture.getCreatedAt());
    assertEquals(newTime, profilePicture.getUpdatedAt());
  }

  @Test
  void shouldHandleNullFieldsGracefully() {
    MentorProfilePicture profilePicture = new MentorProfilePicture();

    assertNull(profilePicture.getId());
    assertNull(profilePicture.getMentorEmail());
    assertNull(profilePicture.getResourceId());
    assertNull(profilePicture.getResource());
    assertNull(profilePicture.getCreatedAt());
    assertNull(profilePicture.getUpdatedAt());
  }

  @Test
  void shouldCompareEqualMentorProfilePictures() {
    UUID uuid = UUID.randomUUID();
    MentorProfilePicture profilePicture1 = MentorProfilePicture.builder().id(uuid).build();
    MentorProfilePicture profilePicture2 = MentorProfilePicture.builder().id(uuid).build();

    assertEquals(profilePicture1, profilePicture2);
    assertEquals(profilePicture1.hashCode(), profilePicture2.hashCode());
  }

  @Test
  void shouldGenerateDifferentHashCodesForDifferentMentorProfilePictures() {
    MentorProfilePicture profilePicture1 =
        MentorProfilePicture.builder().id(UUID.randomUUID()).build();
    MentorProfilePicture profilePicture2 =
        MentorProfilePicture.builder().id(UUID.randomUUID()).build();

    assertNotEquals(profilePicture1.hashCode(), profilePicture2.hashCode());
  }
}
