package com.wcc.platform.controller;

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

@WebMvcTest(FooterController.class)
public class FooterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CmsService mockCmsService;

    @Test
    void testInternalServerError() throws Exception {
        when(mockCmsService.getFooter()).thenThrow(new PlatformInternalException("Invalid Json", new RuntimeException()));

        mockMvc.perform(get("/api/cms/v1/footer")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message", is("Invalid Json")))
                .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/footer")));


    }
}
