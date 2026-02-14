package com.wcc.platform.service;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.type.RoleType;
import com.wcc.platform.repository.UserAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Centralized service to provision user accounts and roles. Checks for an existing user account and
 * either adds a role or creates a new account with the requested role.
 */
@Service
@AllArgsConstructor
public class UserProvisionService {

  private final UserAccountRepository userAccountRepository;

  public void provisionUserRole(final Long memberId, final String email, final RoleType roleType) {
    userAccountRepository
        .findByEmail(email)
        .ifPresentOrElse(
            userAccount -> {
              if (userAccount.getRoles().stream().noneMatch(role -> role.equals(roleType))) {
                userAccount.getRoles().add(roleType);
              }
            },
            () -> {
              final var userAccount = new UserAccount(memberId, email, roleType);
              userAccountRepository.create(userAccount);
            });
  }
}
