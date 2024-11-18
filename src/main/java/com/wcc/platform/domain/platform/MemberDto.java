package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import java.util.List;

/** MemberDto class with common attributes for all community members. */
public record MemberDto(
    String fullName,
    String position,
    String slackDisplayName,
    Country country,
    String city,
    String companyName,
    List<SocialNetwork> network,
    List<Image> images) {}
