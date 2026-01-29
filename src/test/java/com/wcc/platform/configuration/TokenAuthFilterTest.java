package com.wcc.platform.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class TokenAuthFilterTest {

  @Mock private AuthService authService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;

  @InjectMocks private TokenAuthFilter tokenAuthFilter;

  @BeforeEach
  void setup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName(
      "Given valid Bearer token When filter executes Then authentication is set in SecurityContext")
  void givenValidBearerTokenWhenDoFilterInternalThenAuthenticationIsSet()
      throws ServletException, IOException {
    final String token = "validToken123";
    final String authHeader = "Bearer " + token;

    when(request.getHeader("Authorization")).thenReturn(authHeader);

    UserAccount mockUser =
        UserAccount.builder().email("admin@wcc.dev").roles(List.of(RoleType.ADMIN)).build();
    Member member =
        Member.builder()
            .id(1L)
            .fullName("Admin WCC")
            .memberTypes(List.of(MemberType.DIRECTOR))
            .build();
    UserAccount.User user = new UserAccount.User(mockUser, member);

    when(authService.authenticateByTokenWithMember(token)).thenReturn(Optional.of(user));

    tokenAuthFilter.doFilterInternal(request, response, filterChain);

    var authentication = SecurityContextHolder.getContext().getAuthentication();
    verify(authService).authenticateByTokenWithMember(token);
    verify(filterChain).doFilter(request, response);

    assertNotNull(authentication);
    assertTrue(authentication.isAuthenticated());
    assertEquals(user, authentication.getPrincipal());
    final var authorities = authentication.getAuthorities();
    assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN")));
  }

  @Test
  @DisplayName(
      "Given missing Authorization header When filter executes Then authentication is not attempted")
  void givenMissingAuthorizationHeaderWhenDoFilterInternalThenNoAuthenticationAttempted()
      throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn(null);

    tokenAuthFilter.doFilterInternal(request, response, filterChain);

    verify(authService, never()).authenticateByTokenWithMember(anyString());
    verify(filterChain).doFilter(request, response);
    assert SecurityContextHolder.getContext().getAuthentication() == null;
  }

  @Test
  @DisplayName(
      "Given invalid Bearer token When filter executes "
          + "Then authentication is not set in SecurityContext")
  void givenInvalidBearerTokenWhenDoFilterInternalThenAuthenticationNotSet()
      throws ServletException, IOException {
    final String invalidToken = "invalidToken123";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
    when(authService.authenticateByTokenWithMember(invalidToken)).thenReturn(Optional.empty());

    tokenAuthFilter.doFilterInternal(request, response, filterChain);

    verify(authService).authenticateByTokenWithMember(invalidToken);
    verify(filterChain).doFilter(request, response);
    assert SecurityContextHolder.getContext().getAuthentication() == null;
  }

  @Test
  @DisplayName(
      "Given malformed header without Bearer prefix When filter executes "
          + "Then authentication is not attempted")
  void givenMalformedHeaderWithoutBearerWhenDoFilterInternalThenNoAuthenticationAttempted()
      throws ServletException, IOException {
    when(request.getHeader("Authorization")).thenReturn("Token abc123");

    tokenAuthFilter.doFilterInternal(request, response, filterChain);

    verify(authService, never()).authenticateByTokenWithMember(anyString());
    verify(filterChain).doFilter(request, response);
    assert SecurityContextHolder.getContext().getAuthentication() == null;
  }
}
