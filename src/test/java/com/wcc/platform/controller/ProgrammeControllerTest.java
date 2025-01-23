package com.wcc.platform.controller;

import static com.wcc.platform.domain.cms.PageType.PROG_BOOK_CLUB;
import static com.wcc.platform.domain.platform.ProgramType.BOOK_CLUB;
import static com.wcc.platform.factories.SetupProgrammeFactories.createProgrammePageTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.domain.exceptions.ContentNotFoundException;
import com.wcc.platform.factories.MockMvcRequestFactory;
import com.wcc.platform.service.ProgrammeService;
import com.wcc.platform.utils.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Unit test for the programme apis. */
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(ProgrammeController.class)
public class ProgrammeControllerTest {
  public static final String API_PROGRAMME = "/api/cms/v1/program";
  public static final String PROG_TYPE_BOOK_CLUB = "?type=BOOK_CLUB";

  @Autowired private MockMvc mockMvc;

  @MockBean private ProgrammeService service;

  @Test
  void testNotFoundProgram() throws Exception {
    when(service.getProgramme(BOOK_CLUB)).thenThrow(new ContentNotFoundException(BOOK_CLUB));

    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest(
                    String.format("%s%s", API_PROGRAMME, PROG_TYPE_BOOK_CLUB))
                .contentType(APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.message", is("Content of Page page:BOOK_CLUB not found")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/program")));
  }

  @Test
  void testOkResponse() throws Exception {
    var fileName = PROG_BOOK_CLUB.getFileName();
    var expectedJson = FileUtil.readFileAsString(fileName);

    when(service.getProgramme(BOOK_CLUB)).thenReturn(createProgrammePageTest(fileName));

    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest(
                    String.format("%s%s", API_PROGRAMME, PROG_TYPE_BOOK_CLUB))
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }
}
