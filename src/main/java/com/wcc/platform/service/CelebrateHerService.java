package com.wcc.platform.service;

import static com.wcc.platform.domain.cms.PageType.CELEBRATE_HER;
import static com.wcc.platform.domain.cms.PageType.MENTORSHIP;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.aboutUs.CelebrateHerPage;
import com.wcc.platform.domain.cms.pages.mentorship.MentorshipPage;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.repository.PageRepository;
import com.wcc.platform.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CelebrateHerService {

    private final ObjectMapper objectMapper;
    private final PageRepository pageRepository;

    @Autowired
    public CelebrateHerService(final ObjectMapper objectMapper, final PageRepository pageRepository) {
        this.objectMapper = objectMapper;
        this.pageRepository = pageRepository;
    }

    /**
     * API to retrieve information about CelebrateHer.
     *
     * @return CelebrateHer overview page.
     */
    public CelebrateHerPage getCelebrateHer() {
        final var page = pageRepository.findById(CELEBRATE_HER.getId());
        if (page.isPresent()) {
            try {
                return objectMapper.convertValue(page.get(), CelebrateHerPage.class);
            } catch (IllegalArgumentException e) {
                throw new PlatformInternalException(e.getMessage(), e);
            }
        }
        throw new ContentNotFoundException(CELEBRATE_HER);
    }
}
