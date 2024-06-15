package com.wcc.platform.controller;

import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.service.CmsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AboutController.class)
class AboutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CmsService service;

    @Test
    void testNotFound() throws Exception {
        when(service.getTeam()).thenThrow(new ContentNotFoundException("Not Found Exception"));

        mockMvc.perform(get("/api/cms/v1/team")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Not Found Exception")))
                .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/team")));
    }

    @Test
    void testInternalError() throws Exception {
        var internalError = new PlatformInternalException("internal error", new RuntimeException());
        when(service.getTeam()).thenThrow(internalError);

        mockMvc.perform(get("/api/cms/v1/team").contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message", is("Err internal error")))
                .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/team")));
    }

    @Test
    void testCollaboratorNotFound() throws Exception {
        when(service.getCollaborator()).thenThrow(new ContentNotFoundException("Not Found Exception"));

        mockMvc.perform(get("/api/cms/v1/collaborators")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Not Found Exception")))
                .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/collaborators")));
    }

    @Test
    void testCollaboratorInternalError() throws Exception {
        var internalError = new PlatformInternalException("internal error", new RuntimeException());
        when(service.getCollaborator()).thenThrow(internalError);

        mockMvc.perform(get("/api/cms/v1/collaborators").contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message", is("internal error")))
                .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/collaborators")));
    }
}