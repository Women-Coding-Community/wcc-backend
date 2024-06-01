package com.wcc.platform.domain.pages.attributes;

import com.wcc.platform.domain.LeadershipMember;

import java.util.List;

public record MemberByType(List<LeadershipMember> directors,
                           List<LeadershipMember> leads,
                           List<LeadershipMember> evangelists) {
}
