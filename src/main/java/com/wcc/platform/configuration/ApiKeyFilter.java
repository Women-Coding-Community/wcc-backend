package com.wcc.platform.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter for handling API key-based authentication. This filter is executed once per request and
 * checks for the presence and validity of an API key in the request headers or query parameter.
 *
 * <p>The filter operates with the following behavior: - Skips authentication if security is
 * disabled. - Accepts API key via "X-API-KEY" header or "api_key" query parameter (for
 * compatibility with external callers).
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

  private static final String API_KEY_HEADER = "X-API-KEY";
  private static final String API_KEY_QUERY = "api_key";

  private final String apiKey;

  private final boolean securityEnabled;
  private final ObjectMapper objectMapper;

  /** Constructor. */
  public ApiKeyFilter(
      @Value("${security.enabled}") final boolean securityEnabled,
      @Value("${security.api.key}") final String apiKey,
      final @Qualifier("objectMapper") ObjectMapper objectMapper) {
    super();
    this.apiKey = apiKey;
    this.securityEnabled = securityEnabled;
    this.objectMapper = objectMapper;
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
      String requestApiKey = request.getHeader(API_KEY_HEADER);
      if (requestApiKey == null || requestApiKey.isBlank()) {
        // Fallback to query parameter for compatibility with public endpoints
        requestApiKey = request.getParameter(API_KEY_QUERY);
      }
      if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
        final Map<String, String> errorBody = formatUnauthorizedError("Invalid API Key");
        sendUnauthorizedResponse(response, errorBody);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  private Map<String, String> formatUnauthorizedError(final String errorMessage) {
    final Map<String, String> errorResponse = new ConcurrentHashMap<>();
    errorResponse.put("error", "Unauthorized");
    errorResponse.put("message", errorMessage);

    return errorResponse;
  }

  private void sendUnauthorizedResponse(
      final HttpServletResponse response, final Map<String, String> errorResponse)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    objectMapper.writeValue(response.getOutputStream(), errorResponse);
  }
}
