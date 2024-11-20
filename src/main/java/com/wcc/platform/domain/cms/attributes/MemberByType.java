package com.wcc.platform.domain.cms.attributes;

import com.wcc.platform.domain.platform.LeadershipMember;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Community core team members grouped by types. */
public record MemberByType(
    @NotEmpty List<LeadershipMember> directors,
    @NotEmpty List<LeadershipMember> leads,
    @NotEmpty List<LeadershipMember> evangelists) {}
