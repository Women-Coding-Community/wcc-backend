package com.wcc.platform.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

class CorsConfigTest {

  @Test
  @DisplayName(
      "Given allowed origins are configured, when creating CORS configuration source, then it"
          + " should contain the allowed origins")
  void shouldCreateCorsConfigurationSourceWithAllowedOrigins() {
    List<String> allowedOrigins = List.of("http://localhost:3000", "https://example.com");
    CorsConfig corsConfig = new CorsConfig(allowedOrigins);

    CorsConfigurationSource source = corsConfig.corsConfigurationSource();

    assertThat(source).isInstanceOf(UrlBasedCorsConfigurationSource.class);
    CorsConfiguration config =
        ((UrlBasedCorsConfigurationSource) source).getCorsConfigurations().get("/**");
    assertThat(config).isNotNull();
    assertThat(config.getAllowedOrigins()).containsExactlyElementsOf(allowedOrigins);
  }

  @Test
  @DisplayName(
      "Given CORS configuration is created, when checking allowed methods, then it should include"
          + " GET, POST, PUT, PATCH, DELETE, and OPTIONS")
  void shouldConfigureAllowedMethods() {
    List<String> allowedOrigins = List.of("http://localhost:3000");
    CorsConfig corsConfig = new CorsConfig(allowedOrigins);

    CorsConfigurationSource source = corsConfig.corsConfigurationSource();

    CorsConfiguration config =
        ((UrlBasedCorsConfigurationSource) source).getCorsConfigurations().get("/**");
    assertThat(config).isNotNull();
    assertThat(config.getAllowedMethods())
        .containsExactlyInAnyOrder("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
  }

  @Test
  @DisplayName(
      "Given CORS configuration is created, when checking allowed headers, then it should include"
          + " Authorization, Content-Type, Accept, X-Requested-With, and X-API-KEY")
  void shouldConfigureAllowedHeaders() {
    List<String> allowedOrigins = List.of("http://localhost:3000");
    CorsConfig corsConfig = new CorsConfig(allowedOrigins);

    CorsConfigurationSource source = corsConfig.corsConfigurationSource();

    CorsConfiguration config =
        ((UrlBasedCorsConfigurationSource) source).getCorsConfigurations().get("/**");
    assertThat(config).isNotNull();
    assertThat(config.getAllowedHeaders())
        .containsExactlyInAnyOrder(
            "Authorization", "Content-Type", "Accept", "X-Requested-With", "X-API-KEY");
  }

  @Test
  @DisplayName(
      "Given CORS configuration is created, when checking credentials setting, then it should be"
          + " enabled")
  void shouldEnableCredentials() {
    List<String> allowedOrigins = List.of("http://localhost:3000");
    CorsConfig corsConfig = new CorsConfig(allowedOrigins);

    CorsConfigurationSource source = corsConfig.corsConfigurationSource();

    CorsConfiguration config =
        ((UrlBasedCorsConfigurationSource) source).getCorsConfigurations().get("/**");
    assertThat(config).isNotNull();
    assertThat(config.getAllowCredentials()).isTrue();
  }

  @Test
  @DisplayName(
      "Given CORS configuration is created, when checking max age, then it should be set to 3600"
          + " seconds")
  void shouldSetMaxAge() {
    List<String> allowedOrigins = List.of("http://localhost:3000");
    CorsConfig corsConfig = new CorsConfig(allowedOrigins);

    CorsConfigurationSource source = corsConfig.corsConfigurationSource();

    CorsConfiguration config =
        ((UrlBasedCorsConfigurationSource) source).getCorsConfigurations().get("/**");
    assertThat(config).isNotNull();
    assertThat(config.getMaxAge()).isEqualTo(3600L);
  }

  @Test
  @DisplayName(
      "Given CORS configuration is created, when checking path mapping, then it should apply to"
          + " all paths")
  void shouldApplyConfigurationToAllPaths() {
    List<String> allowedOrigins = List.of("http://localhost:3000");
    CorsConfig corsConfig = new CorsConfig(allowedOrigins);

    CorsConfigurationSource source = corsConfig.corsConfigurationSource();

    assertThat(source).isInstanceOf(UrlBasedCorsConfigurationSource.class);
    UrlBasedCorsConfigurationSource urlBasedSource = (UrlBasedCorsConfigurationSource) source;
    assertThat(urlBasedSource.getCorsConfigurations()).containsKey("/**");
  }

  @Test
  @DisplayName(
      "Given multiple allowed origins are configured, when creating CORS configuration, then it"
          + " should contain all origins")
  void shouldHandleMultipleAllowedOrigins() {
    List<String> allowedOrigins =
        List.of(
            "http://localhost:3000",
            "http://localhost:8080",
            "https://prod.example.com",
            "https://staging.example.com");
    CorsConfig corsConfig = new CorsConfig(allowedOrigins);

    CorsConfigurationSource source = corsConfig.corsConfigurationSource();

    CorsConfiguration config =
        ((UrlBasedCorsConfigurationSource) source).getCorsConfigurations().get("/**");
    assertThat(config).isNotNull();
    assertThat(config.getAllowedOrigins()).hasSize(4).containsExactlyElementsOf(allowedOrigins);
  }

  @Test
  @DisplayName(
      "Given no allowed origins are configured, when creating CORS configuration, then it should"
          + " have an empty origins list")
  void shouldHandleEmptyAllowedOrigins() {
    List<String> allowedOrigins = List.of();
    CorsConfig corsConfig = new CorsConfig(allowedOrigins);

    CorsConfigurationSource source = corsConfig.corsConfigurationSource();

    CorsConfiguration config =
        ((UrlBasedCorsConfigurationSource) source).getCorsConfigurations().get("/**");
    assertThat(config).isNotNull();
    assertThat(config.getAllowedOrigins()).isEmpty();
  }
}