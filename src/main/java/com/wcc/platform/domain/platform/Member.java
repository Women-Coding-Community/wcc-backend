package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Image;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Member {
    private String fullName;
    private String position;
    private MemberType memberType;
    private List<Image> images;
    private List<SocialNetwork> network;
}