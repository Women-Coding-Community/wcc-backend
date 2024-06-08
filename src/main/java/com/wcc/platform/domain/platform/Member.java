package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Image;

import java.util.List;


public class Member {
    String fullName;
    String position;
    MemberType memberType;
    List<Image> images;
    List<SocialNetwork> network;

    public Member() {
    }

    public Member(String fullName, String position, MemberType memberType, List<Image> images, List<SocialNetwork> network) {
        this.fullName = fullName;
        this.position = position;
        this.memberType = memberType;
        this.images = images;
        this.network = network;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<SocialNetwork> getNetwork() {
        return network;
    }

    public void setNetwork(List<SocialNetwork> network) {
        this.network = network;
    }
}