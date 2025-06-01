package com.wcc.platform.configuration;

import com.wcc.platform.domain.platform.config.PlatformServers;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Custom configuration for open api. */
@Configuration
@SecurityScheme(
    name = "apiKey",
    type = SecuritySchemeType.APIKEY,
    paramName = "X-API-KEY",
    in = io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER)
public class OpenApiConfig implements WebMvcConfigurer {

  private static final String ACTUATOR = "actuator";
  private static final String ACTUATOR_SPRING = "Spring Boot Actuator";

  private static Consumer<Tag> renameActuator() {
    return tag -> {
      if (ACTUATOR.equalsIgnoreCase(tag.getName())) {
        tag.setName(ACTUATOR_SPRING);
      }
    };
  }

  private static Consumer<Operation> updateOperationTag() {
    return operation -> {
      if (operation.getTags() != null) {
        IntStream.range(0, operation.getTags().size())
            .filter(i -> ACTUATOR.equalsIgnoreCase(operation.getTags().get(i)))
            .forEach(i -> operation.getTags().set(i, ACTUATOR_SPRING));
      }
    };
  }

  /** Group OpenAPI for public APIs. */
  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public")
        .pathsToMatch("/api/**")
        .addOpenApiCustomizer(renameActuatorTagCustomizer())
        .build();
  }

  /** Customize servers for open API. */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .addServersItem(new Server().url(PlatformServers.DEV.getUri()).description("Dev"))
        .addServersItem(new Server().url(PlatformServers.LOCAL.getUri()).description("Localhost"));
  }

  /** Customize Actuator endpoint tag. */
  private OpenApiCustomizer renameActuatorTagCustomizer() {
    return openApi -> {
      if (openApi.getTags() != null) {
        openApi.getTags().forEach(renameActuator());
      }

      if (openApi.getPaths() != null) {
        openApi
            .getPaths()
            .values()
            .forEach(pathItem -> pathItem.readOperations().forEach(updateOperationTag()));
      }
    };
  }
}
