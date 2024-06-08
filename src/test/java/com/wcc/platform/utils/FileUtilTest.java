package com.wcc.platform.utils;

import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {

    @Test
    void testReadFileAsString() {
        String fileContent = FileUtil.readFileAsString("example.txt");

        assertEquals("Line 1\nLine 2\nLine 3", fileContent);
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
        var exception = assertThrows(ContentNotFoundException.class, () -> FileUtil.getFileUri("example_invalid.txt"));

        assertEquals("File example_invalid.txt not found.", exception.getMessage());
    }
}