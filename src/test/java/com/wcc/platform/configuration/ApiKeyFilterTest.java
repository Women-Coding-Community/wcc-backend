package com.wcc.platform.configuration;

import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@WebMvcTest(ApiKeyFilter.class)
class ApiKeyFilterTest {

  @MockBean private FilterChain filterChain;

  @Test
  void shouldAllowRequestWhenSecurityDisabled() throws Exception {
    ApiKeyFilter apiKeyFilter = new ApiKeyFilter(false, "test-api-key");

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getRequestURI()).thenReturn("/api/cms/v1/test");

    apiKeyFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verifyNoInteractions(response);
  }

  @Test
  void shouldAllowRequestWhenApiKeyMatches() throws Exception {
    ApiKeyFilter apiKeyFilter = new ApiKeyFilter(true, "test-api-key");

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getRequestURI()).thenReturn("/api/cms/v1/test");
    when(request.getHeader("X-API-KEY")).thenReturn("test-api-key");

    apiKeyFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verifyNoInteractions(response);
  }

  @Test
  void shouldRejectRequestWhenApiKeyDoesNotMatch() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    PrintWriter writer = mock(PrintWriter.class);

    when(request.getRequestURI()).thenReturn("/api/cms/v1/test");
    when(request.getHeader("X-API-KEY")).thenReturn("invalid-api-key");
    when(response.getWriter()).thenReturn(writer);

    ApiKeyFilter apiKeyFilter = new ApiKeyFilter(true, "test-api-key");
    apiKeyFilter.doFilterInternal(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(writer, times(1)).write("Unauthorized: Invalid API Key");
    verifyNoInteractions(filterChain);
  }

  @Test
  void shouldRejectRequestWhenApiKeyIsMissing() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    PrintWriter writer = mock(PrintWriter.class);

    when(request.getRequestURI()).thenReturn("/api/cms/v1/test");
    when(request.getHeader("X-API-KEY")).thenReturn(null);
    when(response.getWriter()).thenReturn(writer);

    var apiKeyFilter = new ApiKeyFilter(true, "test-api-key");
    apiKeyFilter.doFilterInternal(request, response, filterChain);

    verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(writer, times(1)).write("Unauthorized: Invalid API Key");
    verifyNoInteractions(filterChain);
  }

  @Test
  void shouldAllowRequestForNonProtectedUri() throws Exception {
    ApiKeyFilter apiKeyFilter = new ApiKeyFilter(true, "test-api-key");

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getRequestURI()).thenReturn("/public/test");

    apiKeyFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain, times(1)).doFilter(request, response);
    verifyNoInteractions(response);
  }

  @Configuration
  static class TestConfig {
    @Bean
    public ApiKeyFilter apiKeyFilter(
        final @Value("${security.enabled:false}") boolean securityEnabled,
        final @Value("${security.api.key:test-api-key}") String apiKey) {
      return new ApiKeyFilter(securityEnabled, apiKey);
    }
  }
}
