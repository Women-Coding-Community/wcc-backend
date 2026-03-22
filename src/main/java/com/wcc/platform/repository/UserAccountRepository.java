package com.wcc.platform.repository;

import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.type.RoleType;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing user account entities. Extends the CrudRepository interface for
 * basic CRUD operations and provides additional query method for user-specific use cases. <br>
 * The UserAccount entity is designed to represent an application user linked to a member, including
 * attributes such as email, password hash, roles, and account status.
 */
public interface UserAccountRepository extends CrudRepository<UserAccount, Integer> {
  Optional<UserAccount> findByEmail(String email);

  List<UserAccount> findAll();

  void updateRole(Integer id, List<RoleType> roles);
}
