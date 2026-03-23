package com.wcc.platform.service;

import com.wcc.platform.configuration.PasswordResetProperties;
import com.wcc.platform.domain.auth.PasswordResetToken;
import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.email.EmailRequest;
import com.wcc.platform.domain.exceptions.InvalidTokenException;
import com.wcc.platform.repository.PasswordResetTokenRepository;
import com.wcc.platform.repository.UserAccountRepository;
import com.wcc.platform.repository.UserTokenRepository;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
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
  private static final String RESET_PATH = "/reset-password?token=";

  private final PasswordResetTokenRepository resetTokenRepository;
  private final UserAccountRepository userAccountRepository;
  private final UserTokenRepository userTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private final PasswordResetProperties resetProperties;

  /**
   * Initiates the password reset flow for the given email address. Generates a single-use reset
   * token, stores it, and emails the reset link to the user. If the email is not registered, the
   * method returns silently to avoid leaking account existence information.
   *
   * @param email the email address of the user whose password should be reset
   * @param recipientName the display name to include in the email greeting
   */
  public void requestReset(final String email, final String recipientName) {
    final Optional<UserAccount> userOpt = userAccountRepository.findByEmail(email);
    if (userOpt.isEmpty()) {
      log.warn("Password reset requested for unknown email: {}", email);
      return;
    }

    final UserAccount user = userOpt.get();
    final OffsetDateTime now = OffsetDateTime.now();
    final OffsetDateTime expiresAt = now.plusMinutes(resetProperties.getTtlMinutes());
    final String rawToken = generateToken();

    resetTokenRepository.create(
        PasswordResetToken.builder()
            .token(rawToken)
            .userId(user.getId())
            .issuedAt(now)
            .expiresAt(expiresAt)
            .used(false)
            .build());

    final String resetLink = resetProperties.getBaseUrl() + RESET_PATH + rawToken;

    emailService.sendEmail(
        EmailRequest.builder()
            .to(email)
            .subject("Reset Your Password — Women Coding Community")
            .body(buildEmailBody(recipientName, resetLink))
            .html(true)
            .build());

    log.info("Password reset email sent to: {}", email);
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

  private String buildEmailBody(final String recipientName, final String resetLink) {
    return "<p>Dear " + recipientName + ",</p>"
        + "<p>A password reset has been requested for your <strong>Women Coding Community</strong>"
        + " account.</p>"
        + "<p>Click the link below to set a new password. This link is valid for <strong>"
        + resetProperties.getTtlMinutes() + " minutes</strong> and can only be used once.</p>"
        + "<p><a href=\"" + resetLink + "\">Reset your password</a></p>"
        + "<p>If you did not request a password reset, please ignore this email.</p>"
        + "<p>WCC Team</p>";
  }

  /** Request DTO for the password reset initiation endpoint. */
  public record ResetPasswordRequest(String email, String recipientName) {}

  /** Request DTO for the password reset confirmation endpoint. */
  public record ConfirmPasswordResetRequest(String token, String newPassword) {}

  /** Response DTO returned from both password reset endpoints. */
  public record PasswordResetResponse(String message) {}
}
