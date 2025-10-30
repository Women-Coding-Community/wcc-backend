package com.wcc.platform.domain.auth;

import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.RoleType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain object representing an application user linked to a Member. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount {
  private Integer id;
  private Long memberId;
  private String email;
  private String passwordHash;
  private List<RoleType> roles;
  private boolean enabled;

  /**
   * A record that encapsulates a User within the platform.
   *
   * <p>This record represents the relationship between a {@link UserAccount} and a {@link Member}.
   * A User consists of a user account for authentication purposes and a member entity containing
   * personal and community-related details.
   */
  public record User(UserAccount userAccount, Member member) {}
}
