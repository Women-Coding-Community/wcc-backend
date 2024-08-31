package com.wcc.platform;

import com.wcc.platform.configuration.ObjectMapperConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;

/** Spring application service. */
@SpringBootApplication
@ConfigurationPropertiesScan
@Import(ObjectMapperConfig.class)
public class PlatformApplication {

  public static void main(String[] args) {
    SpringApplication.run(PlatformApplication.class, args);
  }
}
