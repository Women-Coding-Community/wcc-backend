package com.wcc.platform.factories;

import com.wcc.platform.domain.auth.MemberTypeRoleMapper;
import com.wcc.platform.domain.auth.UserAccount;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.type.MemberType;
import com.wcc.platform.domain.platform.type.RoleType;
import java.util.List;
import java.util.Objects;

public class SetupUserAccountFactories {

  public static UserAccount createAdminUserTest() {
    return new UserAccount(1L, "user@example.com", RoleType.ADMIN);
  }

  public static UserAccount createUserAccountTest(final Member member) {
    List<RoleType> roleTypes = getRolesForMember(member);
    return new UserAccount(member.getId(), member.getEmail(), roleTypes.toArray(RoleType[]::new));
  }

  public static UserAccount createUserAccountTest(final RoleType... roles) {
    return new UserAccount(1L, "user@example.com", roles);
  }

  private static List<RoleType> getRolesForMember(final Member member) {
    List<MemberType> memberTypes = member.getMemberTypes();
    Objects.requireNonNull(memberTypes);

    return memberTypes.stream().map(MemberTypeRoleMapper::getRoleForMemberType).toList();
  }
}
