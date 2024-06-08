package com.wcc.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import static com.wcc.platform.domain.cms.ApiConfig.TEAM;

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
        URL resourceUrl = CmsService.class.getClassLoader().getResource(TEAM.getFileName());

        try {
            if (resourceUrl != null) {
                File file = Path.of(resourceUrl.toURI()).toFile();
                return objectMapper.readValue(file, TeamPage.class);
            }
        } catch (URISyntaxException | IOException e) {
            throw new PlatformInternalException(e.getMessage(), e);
        }

        throw new ContentNotFoundException("Team content not found.");
    }

}