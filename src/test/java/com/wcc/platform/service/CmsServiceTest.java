package com.wcc.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.FooterPage;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.cms.pages.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static com.wcc.platform.factories.TestFactories.createFooterPageTest;
import static com.wcc.platform.factories.TestFactories.createTeamPageTest;
import static com.wcc.platform.factories.TestFactories.createCodeOfConductPageTest;
import static com.wcc.platform.factories.TestFactories.createCollaboratorPageTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CmsServiceTest {

    private ObjectMapper objectMapper;
    private CmsService service;

    @BeforeEach
    void setUp() {
        objectMapper = Mockito.mock(ObjectMapper.class);
        service = new CmsService(objectMapper);
    }

    @Test
    void whenGetTeamGivenInvalidJsonThenThrowsInternalException() throws IOException {
        when(objectMapper.readValue(any(File.class), Mockito.eq(TeamPage.class))).thenThrow(new IOException("Invalid JSON"));

        var exception = assertThrows(PlatformInternalException.class, () -> service.getTeam());

        assertEquals("Invalid JSON", exception.getMessage());
    }

    @Test
    void whenGetTeamGivenValidResourceThenReturnValidObjectResponse() throws IOException {
        var teamPage = createTeamPageTest();
        when(objectMapper.readValue(any(File.class), Mockito.eq(TeamPage.class))).thenReturn(teamPage);

        var response = service.getTeam();

        assertEquals(teamPage, response);
    }

    @Test
    void whenGetFooterGivenInvalidJson() throws IOException {
        when(objectMapper.readValue(any(File.class), Mockito.eq(FooterPage.class))).thenThrow(new IOException("Invalid JSON"));
        var exception = assertThrows(PlatformInternalException.class, () -> service.getFooter());

        assertEquals("Invalid JSON", exception.getMessage());
    }

    @Test
    void whenGetFooterGivenValidJson() throws IOException {
        var footer = createFooterPageTest();
        when(objectMapper.readValue(any(File.class), Mockito.eq(FooterPage.class))).thenReturn(footer);

        var response = service.getFooter();

        assertEquals(footer, response);
    }

    @Test
    void whenGetCollabortorGivenInvalidJsonThenThrowsInternalException() throws IOException {
        when(objectMapper.readValue(any(File.class), Mockito.eq(CollaboratorPage.class))).thenThrow(new IOException("Invalid JSON"));

        var exception = assertThrows(PlatformInternalException.class, () -> service.getCollaborator());

        assertEquals("Invalid JSON", exception.getMessage());
    }

    @Test
    void whenGetCollaboratorGivenValidResourceThenReturnValidObjectResponse() throws IOException {
        var collaboratorPage = createCollaboratorPageTest();
        when(objectMapper.readValue(any(File.class), Mockito.eq(CollaboratorPage.class))).thenReturn(collaboratorPage);

        var response = service.getCollaborator();

        assertEquals(collaboratorPage, response);
    }

    @Test
    void whenGetCodeOfConductGivenInvalidJson() throws IOException {
        when(objectMapper.readValue(any(File.class), Mockito.eq(CodeOfConductPage.class))).thenThrow(new IOException("Invalid JSON"));

        var exception = assertThrows(PlatformInternalException.class, () -> service.getCodeOfConduct());

        assertEquals("Invalid JSON", exception.getMessage());
    }

    @Test
    void whenGetCodeOfConductGivenValidJson() throws IOException {
        var codeOfConductPage = createCodeOfConductPageTest();
        when(objectMapper.readValue(any(File.class), Mockito.eq(CodeOfConductPage.class))).thenReturn(codeOfConductPage);

        var response = service.getCodeOfConduct();

        assertEquals(codeOfConductPage, response);
    }
}