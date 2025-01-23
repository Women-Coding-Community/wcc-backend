package com.wcc.platform.factories;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

/** Factory class to add headers to the mock request. */
public class MockMvcRequestFactory {

  private static final String API_KEY_HEADER = "X-API-KEY";
  private static final String API_KEY_VALUE = "test-api-key";

  /**
   * Get request with the header.
   *
   * @param url - endpoint url
   * @return MockMvcRequestBuilders {@link MockMvcRequestBuilders}
   */
  public static MockHttpServletRequestBuilder getRequest(final String url) {
    return MockMvcRequestBuilders.get(url).header(API_KEY_HEADER, API_KEY_VALUE);
  }

  /**
   * Post request with header.
   *
   * @param url - endpoint url
   * @param content - post request body
   * @return MockMvcRequestBuilders {@link MockMvcRequestBuilders}
   * @throws JsonProcessingException parsing exception
   */
  public static MockHttpServletRequestBuilder postRequest(final String url, final Object content)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return MockMvcRequestBuilders.post(url)
        .header(API_KEY_HEADER, API_KEY_VALUE)
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(content));
  }
}
