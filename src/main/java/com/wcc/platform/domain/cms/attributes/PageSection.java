package com.wcc.platform.domain.cms.attributes;

import java.util.List;

public record PageSection(String title, String description, SimpleLink link, List<String> topics) {
}
