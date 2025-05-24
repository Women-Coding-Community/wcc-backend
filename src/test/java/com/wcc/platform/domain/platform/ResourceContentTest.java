package com.wcc.platform.domain.platform;

import static com.wcc.platform.factories.SetupFactories.createImageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.LabelLink;
import com.wcc.platform.domain.platform.type.ResourceType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceContentTest {

  private final List<Image> images = List.of(createImageTest());
  private final LabelLink labelLink = new LabelLink("Test Link", "Test Label", "example.com");

  @Test
  @DisplayName("Given no-args constructor, when initialized, then fields should be null")
  void noArgsConstructor() {
    ResourceContent resource = new ResourceContent();

    assertNotNull(resource);
    assertNull(resource.getId());
    assertNull(resource.getName());
    assertNull(resource.getDescription());
    assertNull(resource.getRawContent());
    assertNull(resource.getType());
    assertNull(resource.getImages());
    assertNull(resource.getLink());
  }

  @Test
  @DisplayName(
      "Given all-args constructor, when initialized, then fields should be correctly assigned")
  void allArgsConstructor() {
    ResourceContent resource =
        new ResourceContent(
            "1",
            "Test Resource",
            "Test Description",
            "Raw Content",
            ResourceType.LINK,
            images,
            labelLink);

    assertEquals("1", resource.getId());
    assertEquals("Test Resource", resource.getName());
    assertEquals("Test Description", resource.getDescription());
    assertEquals("Raw Content", resource.getRawContent());
    assertEquals(ResourceType.LINK, resource.getType());
    assertEquals(images, resource.getImages());
    assertEquals(images, resource.getImages());
    assertEquals(labelLink, resource.getLink());
  }

  @Test
  @DisplayName("Given builder, when fields are set, then fields should be correctly assigned")
  void builder() {
    ResourceContent resource =
        ResourceContent.builder()
            .id("1")
            .name("Test Resource")
            .description("Test Description")
            .rawContent("Raw Content")
            .type(ResourceType.DOCUMENT)
            .images(images)
            .build();

    assertEquals("Test Resource", resource.getName());
    assertEquals("Test Description", resource.getDescription());
    assertEquals("Raw Content", resource.getRawContent());
    assertEquals(ResourceType.DOCUMENT, resource.getType());
    assertEquals(images, resource.getImages());
  }

  @Test
  @DisplayName(
      "Given ResourceContent, when setters are disabled, then setters should throw NoSuchMethodException")
  void settersAreDisabled() {
    ResourceContent resource = new ResourceContent();

    assertThrows(
        NoSuchMethodException.class,
        () -> ResourceContent.class.getDeclaredMethod("setId", String.class));
    assertThrows(
        NoSuchMethodException.class,
        () -> ResourceContent.class.getDeclaredMethod("setName", String.class));
    assertThrows(
        NoSuchMethodException.class,
        () -> ResourceContent.class.getDeclaredMethod("setDescription", String.class));
    assertThrows(
        NoSuchMethodException.class,
        () -> ResourceContent.class.getDeclaredMethod("setRawContent", String.class));
    assertThrows(
        NoSuchMethodException.class,
        () -> ResourceContent.class.getDeclaredMethod("setType", ResourceType.class));
  }

  @Test
  @DisplayName(
      "Given two identical ResourceContent objects, when compared, then they should be equal and hash codes should match")
  void equalsAndHashCode() {
    ResourceContent resource1 =
        new ResourceContent(
            "1",
            "Test Resource",
            "Test Description",
            "Raw Content",
            ResourceType.LINK,
            images,
            labelLink);
    ResourceContent resource2 =
        new ResourceContent(
            "1",
            "Test Resource",
            "Test Description",
            "Raw Content",
            ResourceType.LINK,
            images,
            labelLink);

    assertEquals(resource1, resource2);
    assertEquals(resource1.hashCode(), resource2.hashCode());
  }

  @Test
  @DisplayName(
      "Given ResourceContent, when toString is called, then it should return correct string representation")
  void testToString() {
    ResourceContent resource =
        new ResourceContent(
            "1",
            "Test Resource",
            "Test Description",
            "Raw Content",
            ResourceType.IMAGE,
            images,
            labelLink);

    String expected =
        "ResourceContent(id=1, name=Test Resource, description=Test Description, "
            + "rawContent=Raw Content, type=IMAGE, images=[Image[path=MOBILE.png, "
            + "alt=alt imageMOBILE, type=MOBILE]], link=LabelLink[title=Test Link, label=Test Label, uri=example.com])";

    assertEquals(expected, resource.toString());
  }
}
