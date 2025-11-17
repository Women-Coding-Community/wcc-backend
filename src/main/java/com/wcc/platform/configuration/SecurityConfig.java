package com.wcc.platform.configuration;

import com.wcc.platform.bootstrap.SecurityProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/** Security config for the apis. */
@Configuration
@SuppressWarnings({"PMD.LongVariable"})
public class SecurityConfig {
  private final ApiKeyFilter apiKeyFilter;
  private final ObjectProvider<TokenAuthFilter> tokenAuthFilterProvider;
  private final CorsConfigurationSource corsConfigurationSource;
  private final SecurityProperties properties;

  /**
   * Constructor for the SecurityConfig class, which initializes the necessary filters and
   * configurations for securing the APIs.
   *
   * @param apiKeyFilter the {@link ApiKeyFilter} used to handle API key-based authentication
   * @param tokenAuthFilterProvider the {@link ObjectProvider} for {@link TokenAuthFilter},
   *     responsible for handling token-based authentication when available
   * @param corsConfigurationSource the {@link CorsConfigurationSource} to define and provide CORS
   *     configuration
   */
  public SecurityConfig(
      final ApiKeyFilter apiKeyFilter,
      final ObjectProvider<TokenAuthFilter> tokenAuthFilterProvider,
      final CorsConfigurationSource corsConfigurationSource,
      final SecurityProperties properties) {
    this.apiKeyFilter = apiKeyFilter;
    this.tokenAuthFilterProvider = tokenAuthFilterProvider;
    this.corsConfigurationSource = corsConfigurationSource;
    this.properties = properties;
  }

  /** Filter configuration. */
  @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "squid:S4502"})
  // Suppress Sonar warning for CSRF disable
  @Bean
  public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/api/auth/**",
                        "/actuator/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**")
                    .permitAll()
                    .anyRequest()
                    .permitAll())
        .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class);
    final TokenAuthFilter tokenAuthFilter = tokenAuthFilterProvider.getIfAvailable();
    if (tokenAuthFilter != null) {
      http.addFilterBefore(tokenAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }
    return http.build();
  }

  /**
   * Provides a {@link PasswordEncoder} bean configured with Argon2 algorithm based on security
   * properties. The Argon2PasswordEncoder is specified with parameters such as salt length, hash
   * length, parallelism, memory size, and iterations sourced from the security properties
   * configuration.
   *
   * @return a configured instance of {@link PasswordEncoder} using Argon2
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new Argon2PasswordEncoder(
        properties.getSaltLength(),
        properties.getHashLength(),
        properties.getParallelism(),
        properties.getMemory(),
        properties.getIterations());
  }
}
