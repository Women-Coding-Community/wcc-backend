package com.wcc.platform.domain.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.platform.type.ResourceType;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ResourceTest {

  private final String contentType = "text/plain";

  @Test
  void shouldBuildResourceWithAllFields() {
    UUID uuid = UUID.randomUUID();
    String name = "Resource Name";
    String description = "Resource Description";
    String fileName = "file.txt";
    long size = 1024L;
    String driveFileId = "driveFileId123";
    String driveFileLink = "http://drive.google.com/file/driveFileId123";
    ResourceType resourceType = ResourceType.EVENT_IMAGE;
    OffsetDateTime now = OffsetDateTime.now();

    Resource resource =
        Resource.builder()
            .id(uuid)
            .name(name)
            .description(description)
            .fileName(fileName)
            .contentType(contentType)
            .size(size)
            .driveFileId(driveFileId)
            .driveFileLink(driveFileLink)
            .resourceType(resourceType)
            .createdAt(now)
            .updatedAt(now)
            .build();

    assertEquals(uuid, resource.getId());
    assertEquals(name, resource.getName());
    assertEquals(description, resource.getDescription());
    assertEquals(fileName, resource.getFileName());
    assertEquals(contentType, resource.getContentType());
    assertEquals(size, resource.getSize());
    assertEquals(driveFileId, resource.getDriveFileId());
    assertEquals(driveFileLink, resource.getDriveFileLink());
    assertEquals(resourceType, resource.getResourceType());
    assertEquals(now, resource.getCreatedAt());
    assertEquals(now, resource.getUpdatedAt());
  }

  @Test
  void shouldUpdateResourceFields() {
    UUID newId = UUID.randomUUID();
    String newName = "Updated Resource Name";
    String newDescription = "Updated Description";
    String newFileName = "updated_file.txt";
    long newSize = 2048L;
    String newDriveFileId = "newDriveFileId456";
    String newDriveFileLink = "http://drive.google.com/file/newDriveFileId456";
    ResourceType newResourceType = ResourceType.EVENT_IMAGE;
    OffsetDateTime newTime = OffsetDateTime.now();
    String newContentType = "application/json";

    Resource resource =
        Resource.builder()
            .id(newId)
            .name(newName)
            .description(newDescription)
            .fileName(newFileName)
            .contentType(newContentType)
            .size(newSize)
            .driveFileId(newDriveFileId)
            .driveFileLink(newDriveFileLink)
            .resourceType(newResourceType)
            .createdAt(newTime)
            .updatedAt(newTime)
            .build();

    assertEquals(newId, resource.getId());
    assertEquals(newName, resource.getName());
    assertEquals(newDescription, resource.getDescription());
    assertEquals(newFileName, resource.getFileName());
    assertEquals(newContentType, resource.getContentType());
    assertEquals(newSize, resource.getSize());
    assertEquals(newDriveFileId, resource.getDriveFileId());
    assertEquals(newDriveFileLink, resource.getDriveFileLink());
    assertEquals(newResourceType, resource.getResourceType());
    assertEquals(newTime, resource.getCreatedAt());
    assertEquals(newTime, resource.getUpdatedAt());
  }

  @Test
  void shouldHandleNullFieldsGracefully() {
    Resource resource = new Resource();

    assertNull(resource.getId());
    assertNull(resource.getName());
    assertNull(resource.getDescription());
    assertNull(resource.getFileName());
    assertNull(resource.getContentType());
    assertEquals(0L, resource.getSize());
    assertNull(resource.getDriveFileId());
    assertNull(resource.getDriveFileLink());
    assertNull(resource.getResourceType());
    assertNull(resource.getCreatedAt());
    assertNull(resource.getUpdatedAt());
  }

  @Test
  void shouldCompareEqualResources() {
    UUID uuid = UUID.randomUUID();
    Resource resource1 = Resource.builder().id(uuid).build();
    Resource resource2 = Resource.builder().id(uuid).build();

    assertEquals(resource1, resource2);
    assertEquals(resource1.hashCode(), resource2.hashCode());
  }

  @Test
  void shouldGenerateDifferentHashCodesForDifferentResources() {
    Resource resource1 = Resource.builder().id(UUID.randomUUID()).build();
    Resource resource2 = Resource.builder().id(UUID.randomUUID()).build();

    assertNotEquals(resource1.hashCode(), resource2.hashCode());
  }

  @Test
  void shouldHashCodeBeEqual() {
    Resource resource = new Resource();
    assertEquals(new Resource(), resource);
    assertEquals(new Resource().hashCode(), resource.hashCode());
  }

  @Test
  void shouldHashCodeNotBeEqual() {
    var resource = new Resource();
    var resourceNoEquals = Resource.builder().id(UUID.randomUUID()).build();
    assertNotEquals(resourceNoEquals, resource);
    assertNotEquals(resourceNoEquals.hashCode(), resource.hashCode());
  }

  @Test
  void shouldReturnProperToString() {
    UUID uuid = UUID.randomUUID();
    Resource resource =
        Resource.builder()
            .id(uuid)
            .name("Test Name")
            .description("Test Description")
            .fileName("test.txt")
            .contentType("text/plain")
            .size(123L)
            .driveFileId("fileId")
            .driveFileLink("http://link")
            .resourceType(ResourceType.EVENT_IMAGE)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build();

    String toString = resource.toString();

    assertTrue(toString.contains("Test Name"));
    assertTrue(toString.contains("test.txt"));
    assertTrue(toString.contains("fileId"));
  }
}
