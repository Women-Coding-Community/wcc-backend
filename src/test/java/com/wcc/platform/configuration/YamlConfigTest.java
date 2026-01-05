package com.wcc.platform.configuration;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

class YamlConfigTest {

  @Test
  void yamlObjectMapperTest() {
    YamlConfig cfg = new YamlConfig();
    ObjectMapper mapper = cfg.yamlObjectMapper();

    assertNotNull(mapper, "yamlObjectMapper should not be null");
    assertInstanceOf(YAMLFactory.class, mapper.getFactory(), "Factory should be YAMLFactory");
  }
}
