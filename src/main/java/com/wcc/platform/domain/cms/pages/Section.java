package com.wcc.platform.domain.cms.pages;

import java.util.List;

public record Section(String title, String description, List<String> items) {
}
