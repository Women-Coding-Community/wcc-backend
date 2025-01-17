package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.PageType.MENTORSHIP;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.aboutUs.CelebrateHerPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CelebrateHerService {

    private final ObjectMapper objectMapper;

    @Autowired
    public CelebrateHerService(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * API to retrieve information about celebrateHer overview.
     *
     * @return Celebrate overview page.
     */
    public CelebrateHerPage getOverview() {
        try {
            final String data = FileUtil.readFileAsString(MENTORSHIP.getFileName());
            return objectMapper.readValue(data, CelebrateHerPage.class);
        } catch (JsonProcessingException e) {
            throw new PlatformInternalException(e.getMessage(), e);
        }
    }
}
