package com.wcc.platform.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import org.junit.jupiter.api.Test;

class FileUtilTest {

  @Test
  void testReadFileAsString() {
    String fileContent = FileUtil.readFileAsString("example.txt");
    String testContent =
        "Line 1" + System.lineSeparator() + "Line 2" + System.lineSeparator() + "Line 3";
    assertEquals(testContent, fileContent);
  }

  @Test
  void testReadFileAsStringWhenDoesNotExist() {
    String fileContent = FileUtil.readFileAsString("example1.txt");

    assertEquals("", fileContent);
  }

  @Test
  void testGetUriFromFile() {
    var uri = FileUtil.getFileUri("example.txt");

    assertTrue(uri.getPath().endsWith("resources/test/example.txt"));
  }

  @Test
  void whenFileDoNotExistThrowsException() {
    var exception =
        assertThrows(
            ContentNotFoundException.class, () -> FileUtil.getFileUri("example_invalid.txt"));

    assertEquals("File example_invalid.txt not found.", exception.getMessage());
  }
}
