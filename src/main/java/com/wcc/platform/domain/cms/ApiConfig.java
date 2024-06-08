package com.wcc.platform.domain.cms;

/**
 * Create custom api configurations.
 */
public enum ApiConfig {
    TEAM("TeamPage.json");

    private final String fileName;

    ApiConfig(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
