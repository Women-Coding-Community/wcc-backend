package com.wcc.platform.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  // For resource authentication - TOKEN already provided from the FE application when making a
  // request to the API
  /*@Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/platform/**")
                    .authenticated() // Secure endpoints
                    .anyRequest()
                    .permitAll())
        .oauth2ResourceServer((oauth2) -> oauth2.jwt(withDefaults()));
    return http.build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    return new GitHubTokenValidator(); // Use a custom token validator
  }*/

  /// This flow --Wcc-backend application acts as the OAuth Client.
  // GitHub acts as the OAuth2 Provider (Authorization Server).
  // The user grants permissions via GitHub, and your application uses the resulting access token to
  // protect its resources and fetch user data.

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/platform/v1/**")
                    .authenticated() // Protect API endpoints
                    .anyRequest()
                    .permitAll()) // Public access to other endpoints
        .oauth2Login(Customizer.withDefaults());
    return http.build();
  }
}
