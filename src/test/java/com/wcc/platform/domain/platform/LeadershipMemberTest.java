package com.wcc.platform.domain.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class LeadershipMemberTest {

  private final LeadershipMember director1 =
      LeadershipMember.leadershipMemberBuilder().memberType(MemberType.DIRECTOR).build();

  private final LeadershipMember director2 =
      LeadershipMember.leadershipMemberBuilder().memberType(MemberType.DIRECTOR).build();

  private final LeadershipMember leader =
      LeadershipMember.leadershipMemberBuilder().memberType(MemberType.LEADER).build();

  @Test
  void testEquals() {
    assertEquals(director1, director2);
    assertNotEquals(director1, leader);
  }

  @Test
  void testHashCode() {
    assertEquals(director1.hashCode(), director2.hashCode());
    assertNotEquals(director1.hashCode(), leader.hashCode());
  }

  @Test
  void testToString() {
    var evangelist =
        LeadershipMember.leadershipMemberBuilder().memberType(MemberType.EVANGELIST).build();

    assertEquals("LeadershipMember(memberType=EVANGELIST)", evangelist.toString());
  }
}
