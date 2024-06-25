package com.wcc.platform.controller;

import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.ImageType;
import com.wcc.platform.domain.cms.pages.CodeOfConductPage;
import com.wcc.platform.domain.cms.pages.CollaboratorPage;
import com.wcc.platform.domain.cms.pages.Page;
import com.wcc.platform.domain.cms.pages.Section;
import com.wcc.platform.domain.cms.attributes.Contact;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.domain.platform.Member;
import com.wcc.platform.domain.platform.MemberType;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.SocialNetworkType;
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

import java.util.List;


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
                .andExpect(jsonPath("$.message", is("internal error")))
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
        var internalError = new PlatformInternalException("internal Json", new RuntimeException());
        when(service.getCollaborator()).thenThrow(internalError);

        mockMvc.perform(get("/api/cms/v1/collaborators")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message", is("internal Json")))
                .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/collaborators")));
    }

    @Test
    void testCollaboratorOKResponse() throws Exception {
        var collaborator = new Member();

        collaborator.setFullName("fullName");
        collaborator.setPosition("position");
        collaborator.setMemberType(MemberType.COLLABORATOR);
        collaborator.setImages(List.of(new Image("image.png", "alt image", ImageType.DESKTOP)));
        collaborator.setNetwork(List.of(new SocialNetwork(SocialNetworkType.LINKEDIN, "collaborator_link")));

        var collaboratorPage = new CollaboratorPage(
            new Page("collaborator_title", "collaborator_subtitle", "collaborator_desc"),
            new Contact("contact_title", List.of(new SocialNetwork(SocialNetworkType.LINKEDIN, "page_link"))),
            List.of(collaborator));

        when(service.getCollaborator()).thenReturn(collaboratorPage);

        mockMvc.perform(get("/api/cms/v1/collaborators")
                        .contentType(APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.page.title", is("collaborator_title")))
                        .andExpect(jsonPath("$.page.subtitle", is("collaborator_subtitle")))
                        .andExpect(jsonPath("$.page.description", is("collaborator_desc")))
                        .andExpect(jsonPath("$.contact.title", is("contact_title")))
                        .andExpect(jsonPath("$.contact.links[0].type", is("LINKEDIN")))
                        .andExpect(jsonPath("$.contact.links[0].link", is("page_link")))
                        .andExpect(jsonPath("$.collaborators[0].fullName", is("fullName")))
                        .andExpect(jsonPath("$.collaborators[0].position", is("position")))
                        .andExpect(jsonPath("$.collaborators[0].memberType", is("COLLABORATOR")))
                        .andExpect(jsonPath("$.collaborators[0].images[0].path", is("image.png")))
                        .andExpect(jsonPath("$.collaborators[0].images[0].alt", is("alt image")))
                        .andExpect(jsonPath("$.collaborators[0].images[0].type", is("DESKTOP")))
                        .andExpect(jsonPath("$.collaborators[0].network[0].type", is("LINKEDIN")))
                        .andExpect(jsonPath("$.collaborators[0].network[0].link", is("collaborator_link")));
    }

    @Test
    void testCodeOfConductNotFound() throws Exception {
        when(service.getCodeOfConduct()).thenThrow(new ContentNotFoundException("Not Found Exception"));

        mockMvc.perform(get("/api/cms/v1/code-of-conduct")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Not Found Exception")))
                .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/code-of-conduct")));
    }

    @Test
    void testCodeOfConductInternalError() throws Exception {
        var internalError = new PlatformInternalException("internal Json", new RuntimeException());
        when(service.getCodeOfConduct()).thenThrow(internalError);

        mockMvc.perform(get("/api/cms/v1/code-of-conduct")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message", is("internal Json")))
                .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/code-of-conduct")));
    }

    @Test
    void testCodeOfConductOKResponse() throws Exception {
        var codeOfConductPage = new CodeOfConductPage(
            new Page("code_of_conduct_title", "code_of_conduct_subtitle", "code_of_conduct_desc"),
            List.of(new Section("section_title", "section_description", List.of("item_1", "item_2", "item_3"))));

        when(service.getCodeOfConduct()).thenReturn(codeOfConductPage);

        mockMvc.perform(get("/api/cms/v1/code-of-conduct")
                        .contentType(APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.page.title", is("code_of_conduct_title")))
                        .andExpect(jsonPath("$.page.subtitle", is("code_of_conduct_subtitle")))
                        .andExpect(jsonPath("$.page.description", is("code_of_conduct_desc")))
                        .andExpect(jsonPath("$.items[0].title", is("section_title")))
                        .andExpect(jsonPath("$.items[0].description", is("section_description")))
                        .andExpect(jsonPath("$.items[0].items[0]", is("item_1")))
                        .andExpect(jsonPath("$.items[0].items[1]", is("item_2")))
                        .andExpect(jsonPath("$.items[0].items[2]", is("item_3")));
    }
}