package com.wcc.platform.domain.pages;

import java.util.List;

public record PageData<T>(String title, String subtitle, String description, List<T> items) {
}
