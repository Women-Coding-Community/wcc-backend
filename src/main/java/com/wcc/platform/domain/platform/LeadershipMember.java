package com.wcc.platform.domain.platform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wcc.platform.domain.cms.attributes.Image;

import java.util.List;


public record LeadershipMember(
        String fullName,
        String position,
        @JsonIgnore
        MemberType memberType,
        List<Image> images,
        List<SocialNetwork> network) {
}