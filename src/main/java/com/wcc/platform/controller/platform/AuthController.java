package com.wcc.platform.controller.platform;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.member.MemberDto;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController handles the authentication-related HTTP endpoints, including user authentication
 * and retrieval of current authenticated user details.
 *
 * <p>This controller provides methods to log in with user credentials and retrieve the information
 * of the currently logged-in user.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Platform: Authentication")
@RequiredArgsConstructor
public class AuthController {
  private static final ResponseEntity<LoginResponse> UNAUTHORIZED =
      ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid credentials"));
  private final AuthService authService;

  /**
   * Authenticates a user using their email and password and returns an access token upon successful
   * authentication.
   *
   * @param request the login request containing the user's email and password
   * @return a {@code ResponseEntity} containing the access token and additional user information if
   *     authentication is successful, or a {@code ResponseEntity} with an error message and {@code
   *     HttpStatus.UNAUTHORIZED} if authentication fails
   */
  @PostMapping("/login")
  @Operation(summary = "Authenticate with email/password and receive an access token")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid final LoginRequest request) {
    final var tokenOpt = authService.authenticateAndIssueToken(request.email(), request.password());
    if (tokenOpt.isEmpty()) {
      return UNAUTHORIZED;
    }

    final Optional<UserAccount> userAccount = authService.findUserByEmail(request.email());
    if (userAccount.isEmpty()) {
      return UNAUTHORIZED;
    }

    final var userToken = tokenOpt.get();
    final var user = userAccount.get();
    final var response =
        new LoginResponse(
            userToken.getToken(),
            userToken.getExpiresAt().toString(),
            user.getRoles(),
            authService.getMember(user.getMemberId()));
    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves the details of the currently authenticated user, including their roles and associated
   * member information if available.
   *
   * <p>The method fetches information about the logged-in user
   */
  @GetMapping("/me")
  @Operation(summary = "Get current authenticated user and member info")
  public ResponseEntity<LoginResponse> currentUser() {
    final var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    final var userAccount = authService.findUserByEmail(auth.getName());
    if (userAccount.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    final UserAccount user = userAccount.get();
    final var response =
        new LoginResponse(user.getRoles(), authService.getMember(user.getMemberId()));

    return ResponseEntity.ok(response);
  }

  /**
   * Represents a login request that encapsulates the user's email and password used for
   * authentication.
   */
  public record LoginRequest(@NotNull String email, @NotNull String password) {}

  /**
   * Represents a response object returned after a successful or unsuccessful login attempt.
   *
   * <p>This class encapsulates details including authentication token, token expiry information,
   * user roles, and associated member details if available. It also includes a message for cases
   * such as unsuccessful login attempts or additional information about the response.
   *
   * <p>Primary constructors allow creating instances either with complete authentication details or
   * with an error message indicating the result.
   */
  public record LoginResponse(
      String token, String expiresAt, List<RoleType> roles, MemberDto member, String message) {

    public LoginResponse(final String message) {
      this(null, null, List.of(), null, message);
    }

    public LoginResponse(final List<RoleType> roles, final MemberDto member) {
      this(null, null, roles, member, null);
    }

    public LoginResponse(
        final String token,
        final String expiresAt,
        final List<RoleType> roles,
        final MemberDto member) {
      this(token, expiresAt, roles, member, null);
    }
  }
}
