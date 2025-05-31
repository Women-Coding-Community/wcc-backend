package com.wcc.platform.controller;

import static com.wcc.platform.domain.cms.PageType.MENTORSHIP;
import static com.wcc.platform.domain.cms.PageType.MENTORSHIP_CONDUCT;
import static com.wcc.platform.domain.cms.PageType.STUDY_GROUPS;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorPageTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorshipConductPageTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorshipFaqPageTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorshipPageTest;
import static com.wcc.platform.factories.SetupMentorshipFactories.createMentorshipStudyGroupPageTest;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.SecurityConfig;
import com.wcc.platform.domain.cms.pages.mentorship.MentorsPage;
import com.wcc.platform.domain.exceptions.PlatformInternalException;
import com.wcc.platform.factories.MockMvcRequestFactory;
import com.wcc.platform.service.MentorshipService;
import com.wcc.platform.utils.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Unit test for mentorship apis. */
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(MentorshipController.class)
public class MentorshipControllerTest {

  public static final String API_MENTORSHIP_OVERVIEW = "/api/cms/v1/mentorship/overview";
  public static final String API_MENTORSHIP_FAQ = "/api/cms/v1/mentorship/faq";
  public static final String API_MENTORSHIP_CONDUCT = "/api/cms/v1/mentorship/code-of-conduct";
  public static final String API_STUDY_GROUPS = "/api/cms/v1/mentorship/study-groups";
  public static final String API_MENTORSHIP_MENTORS = "/api/cms/v1/mentorship/mentors";
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private MentorshipService service;

  @Test
  void testInternalServerError() throws Exception {
    when(service.getOverview())
        .thenThrow(new PlatformInternalException("Invalid Json", new RuntimeException()));

    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest(API_MENTORSHIP_OVERVIEW).contentType(APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status", is(500)))
        .andExpect(jsonPath("$.message", is("Invalid Json")))
        .andExpect(jsonPath("$.details", is("uri=/api/cms/v1/mentorship/overview")));
  }

  @Test
  void testOkResponse() throws Exception {
    var fileName = MENTORSHIP.getFileName();
    var expectedJson = FileUtil.readFileAsString(fileName);

    when(service.getOverview()).thenReturn(createMentorshipPageTest(fileName));

    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest(API_MENTORSHIP_OVERVIEW).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void testFaqOkResponse() throws Exception {
    var fileName = "mentorshipFaqPage.json";
    var expectedJson = FileUtil.readFileAsString(fileName);

    when(service.getFaq()).thenReturn(createMentorshipFaqPageTest(fileName));

    mockMvc
        .perform(MockMvcRequestFactory.getRequest(API_MENTORSHIP_FAQ).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void testOkCodeOfConductResponse() throws Exception {
    var fileName = MENTORSHIP_CONDUCT.getFileName();
    var expectedJson = FileUtil.readFileAsString(fileName);

    when(service.getCodeOfConduct()).thenReturn(createMentorshipConductPageTest(fileName));
    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest(API_MENTORSHIP_CONDUCT).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void testStudyGroupResponse() throws Exception {
    var fileName = STUDY_GROUPS.getFileName();
    var expectedJson = FileUtil.readFileAsString(fileName);

    when(service.getStudyGroups()).thenReturn(createMentorshipStudyGroupPageTest(fileName));
    mockMvc
        .perform(MockMvcRequestFactory.getRequest(API_STUDY_GROUPS).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void testMentorsOkResponse() throws Exception {
    MentorsPage mentorsPage = createMentorPageTest();

    when(service.getMentors()).thenReturn(mentorsPage);

    mockMvc
        .perform(
            MockMvcRequestFactory.getRequest(API_MENTORSHIP_MENTORS).contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(mentorsPage)));
  }
}
