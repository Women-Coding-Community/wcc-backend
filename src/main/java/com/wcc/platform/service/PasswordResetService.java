package com.wcc.platform.service;

import com.wcc.platform.configuration.PasswordResetConfig;
import com.wcc.platform.domain.auth.PasswordResetToken;
import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.email.TemplateEmailRequest;
import com.wcc.platform.domain.exceptions.InvalidTokenException;
import com.wcc.platform.domain.template.TemplateType;
import com.wcc.platform.repository.PasswordResetTokenRepository;
import com.wcc.platform.repository.UserAccountRepository;
import com.wcc.platform.repository.UserTokenRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for the password reset flow. Handles generating reset tokens, sending the
 * reset email, and applying the new password once the token is confirmed.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

  private static final SecureRandom RANDOM = new SecureRandom();

  private final PasswordResetTokenRepository resetTokenRepository;
  private final UserAccountRepository userAccountRepository;
  private final UserTokenRepository userTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final PasswordResetConfig passwordResetConfig;

  /**
   * Initiates the password reset flow for the given email address. Generates a single-use reset
   * token, stores it, and emails the reset link to the user.
   *
   * <p>Transactional to roll back the token insert if the email send fails. Trade-off: the email is
   * sent before the transaction commits, so there is a negligible window where the token is not yet
   * visible. A future improvement is to extract token creation to a separate service.
   *
   * @param email the email address of the user whose password should be reset
   * @param recipientName the display name to include in the email greeting
   * @return a message indicating whether the reset email was sent or the user was not found
   */
  @Transactional
  public String requestReset(final String email, final String recipientName) {
    final Optional<UserAccount> userOpt = userAccountRepository.findByEmail(email);
    if (userOpt.isEmpty()) {
      log.warn("Password reset requested for unknown email: {}", email);
      return "If this email is registered, a reset link has been sent";
    }

    final UserAccount user = userOpt.get();
    final OffsetDateTime now = OffsetDateTime.now();
    final OffsetDateTime expiresAt = now.plusMinutes(passwordResetConfig.getTtlMinutes());
    final String rawToken = generateToken();

    resetTokenRepository.create(
        PasswordResetToken.builder()
            .token(rawToken)
            .userId(user.getId())
            .issuedAt(now)
            .expiresAt(expiresAt)
            .used(false)
            .build());

    final String resetLink =
        passwordResetConfig.getBaseUrl() + passwordResetConfig.getResetPath() + rawToken;

    emailService.sendTemplateEmail(
        TemplateEmailRequest.builder()
            .to(email)
            .templateType(TemplateType.RESET_PASSWORD)
            .templateParameters(
                Map.of(
                    "recipientName", recipientName,
                    "resetLink", resetLink))
            .html(true)
            .build());

    return "Password reset email sent";
  }

  /**
   * Completes the password reset by validating the token and updating the user's password. All
   * active session tokens for the user are revoked so existing sessions are invalidated.
   *
   * @param rawToken the single-use reset token from the email link
   * @param newPassword the plain-text new password chosen by the user
   * @throws InvalidTokenException if the token is not found, already used, or expired
   */
  @Transactional
  public void confirmReset(final String rawToken, final String newPassword) {
    final Optional<PasswordResetToken> tokenOpt =
        resetTokenRepository.findValidByToken(rawToken, OffsetDateTime.now());

    if (tokenOpt.isEmpty()) {
      throw new InvalidTokenException("Password reset token is invalid or has expired");
    }

    final PasswordResetToken resetToken = tokenOpt.get();
    final String newHash = passwordEncoder.encode(newPassword);

    userAccountRepository.updatePassword(resetToken.getUserId(), newHash);
    resetTokenRepository.markUsed(rawToken);
    userTokenRepository.revokeAllForUser(resetToken.getUserId());

    log.info("Password reset completed for userId: {}", resetToken.getUserId());
  }

  private String generateToken() {
    final byte[] bytes = new byte[48];
    RANDOM.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  /** Request DTO for the password reset initiation endpoint. */
  public record ResetPasswordRequest(
      @NotBlank @Email String email, @NotBlank String recipientName) {}

  /** Request DTO for the password reset confirmation endpoint. */
  public record ConfirmPasswordResetRequest(
      @NotBlank String token,
      @NotBlank @Size(min = 8, message = "Password must be at least 8 characters")
          String newPassword) {}

  /** Response DTO returned from both password reset endpoints. */
  public record PasswordResetResponse(String message) {}
}
