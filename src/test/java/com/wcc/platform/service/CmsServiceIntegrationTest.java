package com.wcc.platform.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.wcc.platform.domain.cms.ApiResourcesFile.TEAM;
import static com.wcc.platform.factories.TestFactories.createTeamPageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CmsServiceIntegrationTest {

    @Autowired
    private CmsService service;

    @Test
    void getTeamPageTest() {
        var result = service.getTeam();

        var expectedTeamPage = createTeamPageTest(TEAM.getFileName());

        assertEquals(expectedTeamPage, result);
    }
}