package com.wcc.platform.configuration;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.auth.UserAccount.User;
import com.wcc.platform.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * TokenAuthFilter is a Spring Security filter responsible for processing authentication based on a
 * Bearer token. It intercepts each HTTP request, checks for the presence of a valid Authorization
 * header, and delegates token authentication to the AuthService.
 *
 * <p>The filter only processes requests that have the Authorization header starting with "Bearer ".
 *
 * <p>Methods: - doFilterInternal: Handles the filtering logic for token-based authentication. It
 * validates the token and sets the authentication in the SecurityContext if the token is valid.
 */
@Component
@ConditionalOnBean(AuthService.class)
public class TokenAuthFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION = "Authorization";
  private static final String BEARER = "Bearer ";
  private static final int AUTH_TOKEN_START = 7;
  private final AuthService authService;

  public TokenAuthFilter(final AuthService authService) {
    super();
    this.authService = authService;
  }

  /**
   * Processes incoming HTTP requests to handle authentication based on a Bearer token provided in
   * the Authorization header. If a valid token is found and successfully authenticated, an
   * authentication object is created and stored in the SecurityContext. Delegates the request to
   * the next filter in the filter chain after processing.
   *
   * @param request the {@link HttpServletRequest} object containing the client request
   * @param response the {@link HttpServletResponse} object for sending the server response
   * @param filterChain the {@link FilterChain} object allowing the next filter to be invoked
   * @throws ServletException if an error occurs during request processing
   * @throws IOException if an I/O error occurs during request or response handling
   */
  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader(AUTHORIZATION);
    if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER)) {
      final String token = authHeader.substring(AUTH_TOKEN_START);
      final Optional<User> userOpt = authService.authenticateByTokenWithMember(token);
      if (userOpt.isPresent()) {
        final UserAccount.User user = userOpt.get();

        final var authorities = List.of(new SimpleGrantedAuthority(user.getPrimaryRole().name()));
        final UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(user, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    filterChain.doFilter(request, response);
  }
}
