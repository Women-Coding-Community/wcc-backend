package com.wcc.platform.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YamlConfig {

  /** Creates and configures a dedicated ObjectMapper for YAML processing. */
  @Bean
  @Qualifier("yamlObjectMapper")
  public ObjectMapper yamlObjectMapper() {
    return new ObjectMapper(new YAMLFactory());
  }
}
