package com.wcc.platform.domain.cms;

/**
 * Create custom api configurations.
 */
public enum ApiResourcesFile {
    TEAM("TeamPage.json"),
    FOOTER("FooterPage.json"),
    COLLABORATOR("CollaboratorPage.json"),
    VOLUNTEER("Volunteer.json");

    private final String fileName;

    ApiResourcesFile(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
