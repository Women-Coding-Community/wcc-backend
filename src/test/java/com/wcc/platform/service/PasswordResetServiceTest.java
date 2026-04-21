package com.wcc.platform.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcc.platform.configuration.PasswordResetConfig;
import com.wcc.platform.domain.auth.PasswordResetToken;
import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.email.TemplateEmailRequest;
import com.wcc.platform.domain.exceptions.InvalidTokenException;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.PasswordResetTokenRepository;
import com.wcc.platform.repository.UserAccountRepository;
import com.wcc.platform.repository.UserTokenRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

  private static final String EMAIL = "mentor@wcc.dev";
  private static final String RECIPIENT_NAME = "Test Mentor";
  private static final String RAW_TOKEN = "valid-reset-token";
  private static final String NEW_PASSWORD = "NewP@ssword1";
  private static final String HASHED_PASSWORD = "hashed-new-password";

  @Mock private PasswordResetTokenPersistenceService tokenPersistenceService;
  @Mock private PasswordResetTokenRepository resetTokenRepository;
  @Mock private UserAccountRepository userAccountRepository;
  @Mock private UserTokenRepository userTokenRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private EmailService emailService;

  private PasswordResetService passwordResetService;

  @BeforeEach
  void setUp() {
    var resetProperties = new PasswordResetConfig();
    resetProperties.setBaseUrl("http://localhost:3000");
    resetProperties.setTtlMinutes(60);

    passwordResetService =
        new PasswordResetService(
            tokenPersistenceService,
            resetTokenRepository,
            userAccountRepository,
            userTokenRepository,
            passwordEncoder,
            emailService,
            resetProperties);
  }

  @Test
  @DisplayName(
      "Given a valid registered email, when requesting a reset, then token is created and email sent")
  void shouldCreateTokenAndSendEmailForValidEmail() {
    var user = new UserAccount(1, null, EMAIL, "hash", List.of(RoleType.MENTOR), true);
    when(userAccountRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

    passwordResetService.requestReset(EMAIL, RECIPIENT_NAME);

    verify(tokenPersistenceService).persistToken(any(PasswordResetToken.class));
    verify(emailService).sendTemplateEmail(any(TemplateEmailRequest.class));
  }

  @Test
  @DisplayName(
      "Given an unknown email, when requesting a reset, then no token is created and no email sent")
  void shouldSilentlyIgnoreUnknownEmail() {
    when(userAccountRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

    passwordResetService.requestReset(EMAIL, RECIPIENT_NAME);

    verify(tokenPersistenceService, never()).persistToken(any());
    verify(emailService, never()).sendTemplateEmail(any());
  }

  @Test
  @DisplayName(
      "Given a valid unused token, when confirming reset, then password is updated and sessions revoked")
  void shouldUpdatePasswordAndRevokeSessionsOnValidToken() {
    var resetToken =
        PasswordResetToken.builder()
            .token(RAW_TOKEN)
            .userId(1)
            .issuedAt(OffsetDateTime.now().minusMinutes(5))
            .expiresAt(OffsetDateTime.now().plusMinutes(55))
            .used(false)
            .build();

    when(resetTokenRepository.findValidByToken(eq(RAW_TOKEN), any(OffsetDateTime.class)))
        .thenReturn(Optional.of(resetToken));
    when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(HASHED_PASSWORD);

    passwordResetService.confirmReset(RAW_TOKEN, NEW_PASSWORD);

    verify(userAccountRepository).updatePassword(1, HASHED_PASSWORD);
    verify(resetTokenRepository).markUsed(RAW_TOKEN);
    verify(userTokenRepository).revokeAllForUser(1);
  }

  @Test
  @DisplayName(
      "Given an expired or used token, when confirming reset, then InvalidTokenException is thrown")
  void shouldThrowInvalidTokenExceptionForExpiredOrUsedToken() {
    when(resetTokenRepository.findValidByToken(anyString(), any(OffsetDateTime.class)))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> passwordResetService.confirmReset(RAW_TOKEN, NEW_PASSWORD))
        .isInstanceOf(InvalidTokenException.class)
        .hasMessageContaining("invalid or has expired");

    verify(userAccountRepository, never()).updatePassword(any(), anyString());
    verify(userTokenRepository, never()).revokeAllForUser(any());
  }
}
