package com.wcc.platform.domain.platform.type;

import java.util.List;
import java.util.Locale;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/** Accepted types of resources in the platform. */
@AllArgsConstructor
public enum ContentType {
  IMAGE(List.of("jpg", "jpeg", "png", "bmp", "svg", "ico", "tif", "tiff", "webp")),
  DOCUMENT(List.of("text", "pdf", "doc", "docx", "xls", "xlsx", "json")),
  SLIDES(List.of("ppt", "pptx")),
  ZIP(List.of("zip", "gzip", "tar", "tar.gz", "tar.bz2")),
  YOUTUBE(List.of("youtube")),
  LINK(List.of("http", "https")),
  UNDEFINED(List.of());

  private final List<String> extensions;

  /**
   * Parses a string into the corresponding {@code ContentType} enum value. The method performs a
   * case-insensitive comparison of the provided string with the names of the enum values.
   *
   * @param contentType the string representation of the content type to identify
   * @return the matching {@code ContentType} enum value, or {@code UNDEFINED} if no match is found
   */
  public static ContentType fromString(final String contentType) {
    if (!StringUtils.isBlank(contentType)) {
      for (final ContentType type : values()) {
        if (type.name().equalsIgnoreCase(contentType) || hasExtension(contentType, type)) {
          return type;
        }
      }
    }
    return UNDEFINED;
  }

  private static boolean hasExtension(final String contentType, final ContentType type) {
    return type.extensions.stream()
        .anyMatch(extension -> contentType.toLowerCase(Locale.ENGLISH).contains(extension));
  }
}
