package com.wcc.platform.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** Security config for the apis. */
@Configuration
public class SecurityConfig {
  private final ApiKeyFilter apiKeyFilter;

  public SecurityConfig(final ApiKeyFilter apiKeyFilter) {
    this.apiKeyFilter = apiKeyFilter;
  }

  /** Filter configuration. */
  @SuppressWarnings("PMD.SignatureDeclareThrowsException")
  @Bean
  public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
