package com.wcc.platform.domain.cms.attributes;

import com.wcc.platform.domain.platform.SocialNetwork;
import java.util.List;

/** Record for Contact CMS data. */
public record Contact(String title, List<SocialNetwork> links) {}
