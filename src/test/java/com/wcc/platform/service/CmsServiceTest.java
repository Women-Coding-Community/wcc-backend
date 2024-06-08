package com.wcc.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.domain.cms.pages.TeamPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static com.wcc.platform.factories.TestFactories.createTeamPageTest;
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
}