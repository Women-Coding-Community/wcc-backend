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
  void setUp() throws Exception {
    config = new OpenApiConfig();
    // set the private appBaseUrl field used by customOpenApi()
    Field f = OpenApiConfig.class.getDeclaredField("appBaseUrl");
    f.setAccessible(true);
    f.set(config, "https://example.com/base");
  }

  @Test
  void customOpenApi_setsServerUrlFromAppBaseUrl() {
    OpenAPI openAPI = config.customOpenApi();
    assertNotNull(openAPI);

    // verify there's a server with the configured URL
    assertNotNull(openAPI.getServers());
    assertEquals("https://example.com/base", openAPI.getServers().get(0).getUrl());
  }
}
