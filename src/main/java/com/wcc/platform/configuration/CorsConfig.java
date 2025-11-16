package com.wcc.platform.configuration;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** CorsConfig. */
@Configuration
public class CorsConfig {

  @Value("${app.cors.allowed-origins}")
  private List<String> allowedOrigins;

  public CorsConfig(List<String> allowedOrigins) {
    this.allowedOrigins = allowedOrigins;
  }

  /**
   * Configures cross-origin resource sharing (CORS) settings to control how resources on the server
   * can be accessed by external domains.
   *
   * <p>This method sets up the allowed origins, HTTP methods, headers, credentials, and maximum age
   * for CORS preflight requests. The configuration is then applied to all incoming requests by
   * registering it with a `UrlBasedCorsConfigurationSource`.
   *
   * @return the configured `CorsConfigurationSource` instance.
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final var config = new CorsConfiguration();
    config.setAllowedOrigins(allowedOrigins);
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(
        List.of("Authorization", "Content-Type", "Accept", "X-Requested-With", "X-API-KEY"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    final var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
