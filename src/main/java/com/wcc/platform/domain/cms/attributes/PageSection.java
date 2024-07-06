package com.wcc.platform.domain.cms.attributes;

import java.util.List;

/** CMS Page Section which allows to listed related topics. */
public record PageSection(String title, String description, SimpleLink link, List<String> topics) {}
