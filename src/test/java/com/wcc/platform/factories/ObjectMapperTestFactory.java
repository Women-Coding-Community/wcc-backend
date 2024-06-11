package com.wcc.platform.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.ObjectMapperConfig;

public class ObjectMapperTestFactory {

    private static ObjectMapper objectMapper;

    private ObjectMapperTestFactory() {
    }

    public static ObjectMapper getInstance() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapperConfig().objectMapper();
        }

        return objectMapper;
    }
}
