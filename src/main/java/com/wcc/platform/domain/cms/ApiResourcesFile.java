package com.wcc.platform.domain.cms;

/**
 * Create custom api configurations.
 */
public enum ApiResourcesFile {
    TEAM("teamPage.json"),
    MENTORSHIP("mentorshipPage.json"),
    FOOTER("footerPage.json"),
    COLLABORATOR("collaboratorPage.json");
    CODE_OF_CONDUCT("CodeOfConductPage.json");

    private final String fileName;

    ApiResourcesFile(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
