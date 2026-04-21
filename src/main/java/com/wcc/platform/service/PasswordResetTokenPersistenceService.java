package com.wcc.platform.service;

import com.wcc.platform.domain.auth.PasswordResetToken;
import com.wcc.platform.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles persistence of password reset tokens in an isolated transaction. By living in its own
 * Spring bean the {@code REQUIRES_NEW} propagation is honoured via the AOP proxy, ensuring the
 * token is committed to the database before the calling service proceeds to send the reset email.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetTokenPersistenceService {

  private final PasswordResetTokenRepository resetTokenRepository;

  /**
   * Persists the reset token in its own transaction so it is immediately committed and visible to
   * the confirm endpoint, independent of any outer transaction or subsequent email failure.
   *
   * @param token the token to persist
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void persistToken(final PasswordResetToken token) {
    resetTokenRepository.create(token);
  }
}
