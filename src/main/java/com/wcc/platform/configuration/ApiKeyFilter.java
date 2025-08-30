package com.wcc.platform.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter for handling API key-based authentication. This filter is executed once per request and
 * checks for the presence and validity of an API key in the request headers.
 *
 * <p>The filter operates with the following behavior: - Skips authentication if enabled false.
 * Header Details: The API key is expected in the "X-API-KEY" request header.
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

  private static final String API_KEY_HEADER = "X-API-KEY";

  private final String apiKey;

  private final boolean securityEnabled;

  /** Constructor. */
  public ApiKeyFilter(
      @Value("${security.enabled}") final boolean securityEnabled,
      @Value("${security.api.key}") final String apiKey) {
    super();
    this.apiKey = apiKey;
    this.securityEnabled = securityEnabled;
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    final String requestUri = request.getRequestURI();

    if (!securityEnabled) {
      filterChain.doFilter(request, response);
      return;
    }

    if (requestUri.startsWith("/api/cms/v1/") || requestUri.startsWith("/api/platform/v1/")) {
      final String requestApiKey = request.getHeader(API_KEY_HEADER);
      if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized: Invalid API Key");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
