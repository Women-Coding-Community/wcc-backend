package com.wcc.platform.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

  private static final String API_KEY_HEADER = "X-API-KEY";

  @Value("${security.api.key}")
  private String apiKey;

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    String requestUri = request.getRequestURI();

    // Apply the API key check only to the /api/cms/v1/ endpoint
    if (requestUri.startsWith("/api/cms/v1/")) {
      String requestApiKey = request.getHeader(API_KEY_HEADER);
      if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: Invalid API Key");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
