package com.wcc.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.wcc.platform.domain.cms.ApiResourcesFile.FOOTER;
import static com.wcc.platform.domain.cms.ApiResourcesFile.TEAM;

@Service
public class CmsService {
    private final ObjectMapper objectMapper;

    @Autowired
    public CmsService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * API to retrieve information about leadership team members.
     *
     * @return Leadership team page content.
     */
    public TeamPage getTeam() {
        try {
            File file = Path.of(FileUtil.getFileUri(TEAM.getFileName())).toFile();
            return objectMapper.readValue(file, TeamPage.class);
        } catch (IOException e) {
            throw new PlatformInternalException(e.getMessage(), e);
        }
    }

    /**
     * API to retrieve the footer page information.
     *
     * @return Footer page
     */
    public FooterPage getFooter() {
        try {
            File file = Path.of(FileUtil.getFileUri(FOOTER.getFileName())).toFile();
            return objectMapper.readValue(file, FooterPage.class);
        } catch (IOException e) {
            throw new PlatformInternalException(e.getMessage(), e);
        }
    }
}