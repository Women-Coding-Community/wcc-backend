package com.wcc.platform.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
    ObjectMapper objectMapper = mock(ObjectMapper.class);
    ApiKeyFilter apiKeyFilter = new ApiKeyFilter(false, "test-api-key", objectMapper);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getRequestURI()).thenReturn("/api/cms/v1/test");

    apiKeyFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(response);
  }

  @Test
  void shouldAllowRequestWhenApiKeyMatches() throws Exception {
    ObjectMapper objectMapper = mock(ObjectMapper.class);
    ApiKeyFilter apiKeyFilter = new ApiKeyFilter(true, "test-api-key", objectMapper);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getRequestURI()).thenReturn("/api/cms/v1/test");
    when(request.getHeader("X-API-KEY")).thenReturn("test-api-key");

    apiKeyFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(response);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldRejectRequestWhenApiKeyDoesNotMatch() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    ObjectMapper objectMapper = mock(ObjectMapper.class);
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(request.getRequestURI()).thenReturn("/api/cms/v1/test");
    when(request.getHeader("X-API-KEY")).thenReturn("invalid-api-key");
    when(response.getOutputStream()).thenReturn(servletOutputStream);

    ApiKeyFilter apiKeyFilter = new ApiKeyFilter(true, "test-api-key", objectMapper);
    apiKeyFilter.doFilterInternal(request, response, filterChain);

    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json");
    verify(response).setCharacterEncoding("UTF-8");

    ArgumentCaptor<Map<String, String>> mapCaptor = ArgumentCaptor.forClass(Map.class);
    verify(objectMapper).writeValue(eq(servletOutputStream), mapCaptor.capture());

    Map<String, String> actualMap = mapCaptor.getValue();
    assertThat(actualMap.get("error")).isEqualTo("Unauthorized");
    assertThat(actualMap.get("message")).isEqualTo("Invalid API Key");

    verifyNoInteractions(filterChain);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldRejectRequestWhenApiKeyIsMissing() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    ObjectMapper objectMapper = mock(ObjectMapper.class);
    ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);

    when(request.getRequestURI()).thenReturn("/api/cms/v1/test");
    when(request.getHeader("X-API-KEY")).thenReturn(null);
    when(response.getOutputStream()).thenReturn(servletOutputStream);

    var apiKeyFilter = new ApiKeyFilter(true, "test-api-key", objectMapper);
    apiKeyFilter.doFilterInternal(request, response, filterChain);

    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json");
    verify(response).setCharacterEncoding("UTF-8");

    ArgumentCaptor<Map<String, String>> mapCaptor = ArgumentCaptor.forClass(Map.class);
    verify(objectMapper).writeValue(eq(servletOutputStream), mapCaptor.capture());

    Map<String, String> actualMap = mapCaptor.getValue();
    assertThat(actualMap.get("error")).isEqualTo("Unauthorized");
    assertThat(actualMap.get("message")).isEqualTo("Invalid API Key");

    verifyNoInteractions(filterChain);
  }

  @Test
  void shouldAllowRequestForNonProtectedUri() throws Exception {
    ObjectMapper objectMapper = mock(ObjectMapper.class);
    ApiKeyFilter apiKeyFilter = new ApiKeyFilter(true, "test-api-key", objectMapper);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getRequestURI()).thenReturn("/public/test");

    apiKeyFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(response);
  }

  @Configuration
  static class TestConfig {
    @Bean
    public ApiKeyFilter apiKeyFilter(
        final @Value("${security.enabled:false}") boolean securityEnabled,
        final @Value("${security.api.key:test-api-key}") String apiKey,
        final ObjectMapper objectMapper) {
      return new ApiKeyFilter(securityEnabled, apiKey, objectMapper);
    }
  }
}
