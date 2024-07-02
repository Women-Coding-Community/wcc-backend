package com.wcc.platform.domain.cms.attributes;

import com.wcc.platform.domain.platform.LeadershipMember;
import java.util.List;

/** Community core team members grouped by types. */
public record MemberByType(
    List<LeadershipMember> directors,
    List<LeadershipMember> leads,
    List<LeadershipMember> evangelists) {}
