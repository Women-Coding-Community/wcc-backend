package com.wcc.platform.domain.platform;

import static com.wcc.platform.factories.SetupFactories.createMemberTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = createMemberTest(MemberType.MEMBER);
    }

    @Test
    void testEquals() {
        assertEquals(member, createMemberTest(MemberType.MEMBER));
    }

    @Test
    void testNotEquals() {
        assertNotEquals(member, createMemberTest(MemberType.DIRECTOR));
        assertNotEquals(member, new LeadershipMember());
        assertNotEquals(member, new Member());
    }

    @Test
    void testHashCode() {
        assertEquals(member.hashCode(), createMemberTest(MemberType.MEMBER).hashCode());
    }

    @Test
    void testHashCodeNotEquals() {
        assertNotEquals(member.hashCode(), createMemberTest(MemberType.DIRECTOR).hashCode());
    }

    @Test
    void testToString() {
        assertTrue(member.toString().contains(MemberType.MEMBER.toString()));
    }
}