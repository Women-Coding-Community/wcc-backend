package com.wcc.platform.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.Volunteer;
import com.wcc.platform.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.wcc.platform.domain.cms.ApiResourcesFile.*;

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

    /**
     * API to retrieve information about collaborators.
     *
     * @return Collaborators page content.
     */
    public CollaboratorPage getCollaborator() {
        try {
            File file = Path.of(FileUtil.getFileUri(COLLABORATOR.getFileName())).toFile();
            return objectMapper.readValue(file, CollaboratorPage.class);
        } catch (IOException e) {
            throw new PlatformInternalException(e.getMessage(), e);
        }
    }

    /**
     * API to save information about volunteer.
     */
    public Volunteer createVolunteer(Volunteer volunteer) {
        try {
            File file = Path.of(FileUtil.getFileUri(VOLUNTEER.getFileName())).toFile();
            List<Volunteer> volunteers;

            if (file.length() > 0) {
                volunteers = objectMapper.readValue(file, new TypeReference<List<Volunteer>>() {
                });
            } else {
                volunteers = new ArrayList<>();
            }

            volunteers.add(volunteer);

            objectMapper.writeValue(file, volunteers);
            return volunteer;
        } catch (IOException e) {
            throw new PlatformInternalException(e.getMessage(), e);
        }
    }
}