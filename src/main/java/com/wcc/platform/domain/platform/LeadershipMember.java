package com.wcc.platform.domain.platform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wcc.platform.domain.cms.attributes.Image;

import java.util.List;

public class LeadershipMember extends Member {
    public LeadershipMember(String fullName, String position, MemberType memberType, List<Image> images, List<SocialNetwork> network) {
        super(fullName, position, memberType, images, network);
    }

    public LeadershipMember() {
        super();
    }

    @JsonIgnore
    @Override
    public MemberType getMemberType() {
        return super.getMemberType();
    }

    @Override
    public String toString() {
        return "LeadershipMember{" +
                "fullName='" + fullName + '\'' +
                ", memberType=" + memberType +
                '}';
    }
}