package com.wcc.platform.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.swagger.v3.oas.models.OpenAPI;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

  private OpenApiConfig config;

  @BeforeEach
  void setUp() {
    config = new OpenApiConfig();
    // set the private appBaseUrl field used by customOpenApi()
    Field field = null;
    try {
      field = OpenApiConfig.class.getDeclaredField("appBaseUrl");
      field.setAccessible(true);
      field.set(config, "https://example.com/base");
    } catch (NoSuchFieldException | IllegalAccessException e) {
      // do nothing
    }
  }

  @Test
  void customOpenApiSetsServerUrlFromAppBaseUrl() {
    OpenAPI openAPI = config.customOpenApi();
    assertNotNull(openAPI);

    // verify there's a server with the configured URL
    assertNotNull(openAPI.getServers());
    assertEquals("https://example.com/base", openAPI.getServers().get(0).getUrl());
  }
}
