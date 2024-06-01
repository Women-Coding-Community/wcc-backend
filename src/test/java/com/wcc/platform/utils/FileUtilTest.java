package com.wcc.platform.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}