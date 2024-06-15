package com.wcc.platform.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.wcc.platform.domain.cms.ApiResourcesFile.TEAM;
import static com.wcc.platform.domain.cms.ApiResourcesFile.COLLABORATOR;
import static com.wcc.platform.factories.TestFactories.createTeamPageTest;
import static com.wcc.platform.factories.TestFactories.createCollaboratorPageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class CmsServiceIntegrationTest {

    @Autowired
    private CmsService service;

    @Test
    void getTeamPageTest() {
        var result = service.getTeam();

        var expectedTeamPage = createTeamPageTest(TEAM.getFileName());

        assertEquals(expectedTeamPage.page(), result.page());
        assertEquals(expectedTeamPage.contact(), result.contact());

        assertEquals(1, result.membersByType().directors().size());
        assertEquals(1, result.membersByType().leads().size());
        assertEquals(1, result.membersByType().evangelists().size());

        assertNull(result.membersByType().directors().get(0).getMemberType());
        assertNull(result.membersByType().leads().get(0).getMemberType());
        assertNull(result.membersByType().evangelists().get(0).getMemberType());
    }

    @Test
    void getCollaboratorPageTest() {
        var result = service.getCollaborator();

        var expectedCollaboratorPage = createCollaboratorPageTest(COLLABORATOR.getFileName());

        assertEquals(expectedCollaboratorPage.page(), result.page());
        assertEquals(expectedCollaboratorPage.contact(), result.contact());

        assertEquals(1, result.collaborators().size());

        assertNotNull(result.collaborators().get(0).getMemberType());
    }
}