package com.wcc.platform.domain.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.platform.type.ContentType;
import com.wcc.platform.domain.platform.type.ResourceType;
import com.wcc.platform.domain.resource.MentorResource;
import com.wcc.platform.domain.resource.Resource;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceContentTest {

  private static final String RESOURCE_ID = "1";
  private final LabelLink labelLink = new LabelLink("Test Link", "Test Label", "example.com");
  private final List<String> books = List.of("book 1", "book 2");
  private final List<LabelLink> links = List.of(labelLink);
  private final List<Resource> resources =
      List.of(
          Resource.builder()
              .contentType(ContentType.IMAGE)
              .resourceType(ResourceType.MENTOR_RESOURCE)
              .build());

  @Test
  @DisplayName("Given no-args constructor, when initialized, then fields should be null")
  void noArgsConstructor() {
    MentorResource resource = new MentorResource();

    assertNotNull(resource);
    assertNull(resource.getId());
    assertNull(resource.getResources());
    assertNull(resource.getBooks());
    assertNull(resource.getLinks());
  }

  @Test
  @DisplayName(
      "Given all-args constructor, when initialized, then fields should be correctly assigned")
  void allArgsConstructor() {
    var resource = new MentorResource(RESOURCE_ID, books, links, resources);

    assertEquals(RESOURCE_ID, resource.getId());
    assertEquals(books, resource.getBooks());
    assertEquals(links, resource.getLinks());
    assertEquals(resources, resource.getResources());
  }

  @Test
  @DisplayName("Given builder, when fields are set, then fields should be correctly assigned")
  void builder() {
    var resource =
        MentorResource.builder()
            .books(books)
            .links(links)
            .resources(resources)
            .id(RESOURCE_ID)
            .build();

    assertEquals(RESOURCE_ID, resource.getId());
    assertEquals(books, resource.getBooks());
    assertEquals(links, resource.getLinks());
    assertEquals(resources, resource.getResources());
  }

  @Test
  @DisplayName(
      "Given two identical ResourceContent objects, when compared, "
          + "Then they should be equal and hash codes should match")
  void equalsAndHashCode() {
    var resource = new MentorResource(RESOURCE_ID, books, links, resources);
    var resource1 =
        MentorResource.builder()
            .books(books)
            .links(links)
            .resources(resources)
            .id(RESOURCE_ID)
            .build();

    assertEquals(resource1, resource);
    assertEquals(resource1.hashCode(), resource.hashCode());
  }

  @Test
  @DisplayName(
      "Given ResourceContent, when toString is called, "
          + "Then it should return correct string representation")
  void testToString() {
    var resource = new MentorResource(RESOURCE_ID, books, links, resources);
    var resource1 =
        MentorResource.builder()
            .books(books)
            .links(links)
            .resources(resources)
            .id(RESOURCE_ID)
            .build();

    assertEquals(resource1.toString(), resource.toString());
  }
}
