package com.wcc.platform.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.ObjectMapperConfig;

/** Object mapper factory test. */
public class ObjectMapperTestFactory {

  private static ObjectMapper objectMapper;

  private ObjectMapperTestFactory() {}

  /** initialize the test mapper with the same config used by spring. */
  public static ObjectMapper getInstance() {
    if (objectMapper == null) {
      objectMapper = new ObjectMapperConfig().objectMapper();
    }

    return objectMapper;
  }
}
