package com.wcc.platform.bootstrap;

import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.platform.type.RoleType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines a single user account to be seeded at application startup. Bound from each entry under
 * the {@code app.seed.users} configuration list.
 */
@Getter
@Setter
public class SeedUser {

  private boolean enabled;
  private String email;
  private String password;
  private String fullName;
  private List<RoleType> roles;
  private List<MemberType> memberTypes;
}
