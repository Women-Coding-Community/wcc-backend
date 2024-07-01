package com.wcc.platform.utils;

import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * Read content from file and convert to String.
     *
     * @param fileName file name path
     * @return content of the file as String object
     */
    public static String readFileAsString(String fileName) {
        var classLoader = FileUtil.class.getClassLoader();

        try {
            InputStream inputStream = classLoader.getResourceAsStream(fileName);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));

                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }

        } catch (Exception e) {
            logger.error("Exception to read file {}", e.getMessage());
        }

        return Strings.EMPTY;
    }

    /**
     * Get file absolut path based in project resource folder.
     *
     * @param fileName file name path
     * @return file URI from resource class loader
     */
    public static URI getFileUri(String fileName) {
        URL resourceUrl = FileUtil.class.getClassLoader().getResource(fileName);

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