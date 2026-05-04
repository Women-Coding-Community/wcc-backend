package com.wcc.platform.controller.platform;

import static com.wcc.platform.domain.auth.Permission.MEMBER_WRITE;
import static com.wcc.platform.domain.auth.Permission.USER_READ;
import static com.wcc.platform.domain.auth.Permission.USER_WRITE;

import com.wcc.platform.configuration.security.LogicalOperator;
import com.wcc.platform.configuration.security.RequiresPermission;
import com.wcc.platform.domain.auth.LoginResponse;
import com.wcc.platform.domain.auth.UpdateUserRolesRequest;
import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.service.AuthService;
import com.wcc.platform.service.MemberService;
import com.wcc.platform.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@SuppressWarnings("PMD.ExcessiveImports")
public class AuthController {
  private static final ResponseEntity<LoginResponse> UNAUTHORIZED =
      ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse("Invalid credentials"));
  private final AuthService authService;
  private final MemberService memberService;
  private final PasswordResetService passwordResetService;

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
  @Operation(
      summary = "Get current authenticated user and member info",
      security = {@SecurityRequirement(name = "apiKey"), @SecurityRequirement(name = "bearerAuth")})
  public ResponseEntity<LoginResponse> currentUser() {
    final var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    if (!(auth.getPrincipal() instanceof UserAccount.User user)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    final var userAccount = authService.findUserByEmail(user.userAccount().getEmail());
    if (userAccount.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    final UserAccount account = userAccount.get();
    final var response =
        new LoginResponse(account.getRoles(), authService.getMember(account.getMemberId()));

    return ResponseEntity.ok(response);
  }

  /**
   * Updates the roles assigned to an existing user account.
   *
   * <p>Restricted to ADMIN and MENTORSHIP_ADMIN roles. Replaces all existing roles with the
   * provided list.
   *
   * @param userId the ID of the user account to update
   * @param request the request containing the new roles to assign
   * @return the updated {@link UserAccount}
   */
  @PutMapping("/users/{userId}/roles")
  @Operation(
      summary = "Update roles for an existing user account",
      security = {@SecurityRequirement(name = "apiKey"), @SecurityRequirement(name = "bearerAuth")})
  @RequiresPermission({USER_WRITE})
  public ResponseEntity<UserAccount> updateUserRoles(
      @PathVariable final Integer userId,
      @RequestBody @Valid final UpdateUserRolesRequest request) {
    return ResponseEntity.ok(authService.updateUserRoles(userId, request.roles()));
  }

  /**
   * API to retrieve information users with access to platform restrict area.
   *
   * @return List of all members.
   */
  @GetMapping("/users")
  @Operation(
      summary = "API to retrieve users with access to restrict area",
      security = {@SecurityRequirement(name = "apiKey"), @SecurityRequirement(name = "bearerAuth")})
  @RequiresPermission(USER_READ)
  public ResponseEntity<List<UserAccount>> getUsers() {
    return ResponseEntity.ok(memberService.getUsers());
  }

  /**
   * Initiates a password reset by sending a reset link to the specified user's email. Restricted to
   * ADMIN and LEADER roles.
   *
   * @param request the reset request containing the target email and recipient display name
   * @return 200 OK with a message indicating whether the reset email was sent or the user was not
   *     found
   */
  @PostMapping("/reset-password/request")
  @Operation(
      summary = "Send a password reset link to a registered user (admin/leader only)",
      security = {@SecurityRequirement(name = "apiKey"), @SecurityRequirement(name = "bearerAuth")})
  @RequiresPermission(
      value = {MEMBER_WRITE, USER_WRITE},
      operator = LogicalOperator.OR)
  public ResponseEntity<PasswordResetResponse> requestPasswordReset(
      @RequestBody @Valid final ResetPasswordRequest request) {
    final String message =
        passwordResetService.requestReset(request.email(), request.recipientName());
    return ResponseEntity.ok(new PasswordResetResponse(message));
  }

  /**
   * Confirms a password reset by validating the single-use token and applying the new password.
   * This endpoint is public — the token itself is the proof of identity.
   *
   * @param request the confirmation request containing the reset token and the new password
   * @return 200 OK with a confirmation message
   */
  @PostMapping("/reset-password/confirm")
  @Operation(summary = "Confirm password reset using a single-use token from the reset email")
  public ResponseEntity<PasswordResetResponse> confirmPasswordReset(
      @RequestBody @Valid final ConfirmPasswordResetRequest request) {
    passwordResetService.confirmReset(request.token(), request.newPassword());
    return ResponseEntity.ok(new PasswordResetResponse("Password has been reset successfully"));
  }

  /** Request DTO for the password reset initiation endpoint. */
  public record ResetPasswordRequest(
      @NotBlank @Email String email, @NotBlank String recipientName) {}

  /** Request DTO for the password reset confirmation endpoint. */
  public record ConfirmPasswordResetRequest(
      @NotBlank String token,
      @NotBlank
          @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
          @Pattern(
              regexp = "^(?=.*[0-9])(?=.*[!@#$%]).*$",
              message =
                  "Password must contain at least one digit and one special character (!@#$%)")
          String newPassword) {}

  /** Response DTO returned from both password reset endpoints. */
  public record PasswordResetResponse(String message) {}

  /** Represents a login request that encapsulates the user's email and password. */
  public record LoginRequest(@NotNull String email, @NotNull String password) {}
}
