package com.wcc.platform.controller;

import static com.wcc.platform.domain.cms.ApiResourcesFile.PROG_BOOK_CLUB;
import static com.wcc.platform.factories.SetupProgrammeFactories.createProgrammePageTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcc.platform.domain.cms.attributes.ProgramType;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.service.ProgrammeService;
import com.wcc.platform.utils.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/** Unit test for the programme apis. */
@WebMvcTest(ProgrammeController.class)
public class ProgrammeControllerTest {
  public static final String API_PROGRAMME = "/api/cms/v1/programme";
  public static final String PROG_TYPE_BOOK_CLUB = "?type=BOOK_CLUB";
  @Autowired private MockMvc mockMvc;

  @MockBean private ProgrammeService service;

  @Test
  void testInternalServerError() throws Exception {
    when(service.getProgramme(ProgramType.BOOK_CLUB))
        .thenThrow(new PlatformInternalException("Invalid Json", new RuntimeException()));

    mockMvc
        .perform(
            get(String.format("%s%s", API_PROGRAMME, PROG_TYPE_BOOK_CLUB))
                .contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("Invalid Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/programme")));
  }

  @Test
  void testOkResponse() throws Exception {
    var fileName = PROG_BOOK_CLUB.getFileName();
    var expectedJson = FileUtil.readFileAsString(fileName);

    when(service.getProgramme(ProgramType.BOOK_CLUB)).thenReturn(createProgrammePageTest(fileName));

    mockMvc
        .perform(
            get(String.format("%s%s", API_PROGRAMME, PROG_TYPE_BOOK_CLUB))
                .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }
}
