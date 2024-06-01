package com.wcc.platform.utils;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static String readFileAsString(String fileName) {
        var classLoader = FileUtil.class.getClassLoader();

        try {
            InputStream inputStream = classLoader.getResourceAsStream(fileName);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }

        } catch (Exception e) {
            logger.error("Exception to read file {}", e.getMessage());
        }

        return Strings.EMPTY;
    }
}