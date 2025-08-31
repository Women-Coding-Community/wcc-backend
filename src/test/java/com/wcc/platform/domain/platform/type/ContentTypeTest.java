package com.wcc.platform.domain.platform.type;

import static com.wcc.platform.domain.platform.type.ContentType.DOCUMENT;
import static com.wcc.platform.domain.platform.type.ContentType.IMAGE;
import static com.wcc.platform.domain.platform.type.ContentType.LINK;
import static com.wcc.platform.domain.platform.type.ContentType.SLIDES;
import static com.wcc.platform.domain.platform.type.ContentType.ZIP;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class ContentTypeTest {

  private static Stream<Arguments> provideValidContentTypeInputs() {
    return Stream.of(
        // Exact enum name matches
        Arguments.of("IMAGE", IMAGE),
        Arguments.of("DOCUMENT", DOCUMENT),
        Arguments.of("SLIDES", SLIDES),
        Arguments.of("ZIP", ZIP),
        Arguments.of("YOUTUBE", ContentType.YOUTUBE),
        Arguments.of("LINK", LINK),

        // Extension-based matches
        Arguments.of("example.jpeg", IMAGE),
        Arguments.of("file.pdf", DOCUMENT),
        Arguments.of("presentation.ppt", SLIDES),
        Arguments.of("archive.zip", ZIP),
        Arguments.of("youtube", ContentType.YOUTUBE),
        Arguments.of("http://example.com", LINK),
        Arguments.of("https://example.com", LINK),

        // MIME type inputs
        Arguments.of("text/plain", DOCUMENT),
        Arguments.of("application/json", DOCUMENT),
        Arguments.of("image/jpeg", IMAGE),

        // Additional image formats
        Arguments.of("image.png", IMAGE),
        Arguments.of("photo.jpg", IMAGE),
        Arguments.of("icon.svg", IMAGE),

        // Additional document formats
        Arguments.of("document.docx", DOCUMENT),
        Arguments.of("spreadsheet.xlsx", DOCUMENT),

        // Additional slide formats
        Arguments.of("slides.pptx", SLIDES),

        // Additional archive formats
        Arguments.of("backup.tar", ZIP),
        Arguments.of("compressed.gzip", ZIP));
  }

  @ParameterizedTest
  @MethodSource("provideValidContentTypeInputs")
  void fromStringShouldReturnCorrectContentTypeWhenInputIsValid(
      final String input, final ContentType expectedType) {
    // Act
    ContentType result = ContentType.fromString(input);

    // Assert
    assertEquals(expectedType, result);
  }

  @ParameterizedTest
  @NullAndEmptySource
  void fromStringShouldReturnUndefinedWhenInputIsNullOrEmpty(final String input) {
    // Act
    ContentType result = ContentType.fromString(input);

    // Assert
    assertEquals(ContentType.UNDEFINED, result);
  }

  @Test
  void fromStringShouldReturnUndefinedWhenInputIsNull() {
    // Arrange
    String input = null;

    // Act
    ContentType result = ContentType.fromString(input);

    // Assert
    assertEquals(ContentType.UNDEFINED, result);
  }
}
