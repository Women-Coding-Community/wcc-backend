package com.wcc.platform.domain.cms.pages;

import java.util.List;

public record PageData<T>(String title, String subtitle, String description, List<T> items) {

}
