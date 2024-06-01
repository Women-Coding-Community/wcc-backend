package com.wcc.platform.domain.pages.attributes;

import com.wcc.platform.domain.SocialNetwork;

import java.util.List;

public record Contact(String title, List<SocialNetwork> links) {
}
