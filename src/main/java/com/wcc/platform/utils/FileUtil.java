package com.wcc.platform.utils;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

/** Util class to read and write files. */
@Slf4j
public final class FileUtil {

  private FileUtil() {}

  /**
   * Read content from file and convert to String.
   *
   * @param fileName file name path
   * @return content of the file as String object
   */
  public static String readFileAsString(final String fileName) {
    Objects.requireNonNull(fileName, "fileName cannot be null");

    try (InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(fileName)) {
      if (inputStream != null) {

        try (var reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {
          return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
      }
    } catch (IOException e) {
      log.error("Exception to read file " + e.getMessage());
    }

    return Strings.EMPTY;
  }

  /**
   * Get file absolut path based in project resource folder.
   *
   * @param fileName file name path
   * @return file URI from resource class loader
   */
  public static URI getFileUri(final String fileName) {
    Objects.requireNonNull(fileName, "fileName cannot be null");

    final URL resourceUrl = FileUtil.class.getClassLoader().getResource(fileName);

    if (resourceUrl != null) {
      try {
        return resourceUrl.toURI();
      } catch (URISyntaxException e) {
        throw new PlatformInternalException("File URI syntax invalid", e);
      }
    }

    throw new ContentNotFoundException("File " + fileName + " not found.");
  }
}
