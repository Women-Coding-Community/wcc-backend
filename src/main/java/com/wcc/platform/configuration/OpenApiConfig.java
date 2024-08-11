package com.wcc.platform.configuration;

import com.wcc.platform.domain.platform.config.PlatformServers;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Custom configuration for open api. */
@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder().group("public").pathsToMatch("/api/**").build();
  }

  /** Customize servers for open API. */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .addServersItem(new Server().url(PlatformServers.DEV.getUri()).description("Dev"))
        .addServersItem(new Server().url(PlatformServers.LOCAL.getUri()).description("Localhost"));
  }
}
