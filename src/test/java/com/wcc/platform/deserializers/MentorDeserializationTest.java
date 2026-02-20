package com.wcc.platform.deserializers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.ObjectMapperConfig;
import com.wcc.platform.domain.cms.pages.mentorship.MentorMonthAvailability;
import com.wcc.platform.domain.platform.mentorship.Mentor;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for Mentor deserialization. */
class MentorDeserializationTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapperConfig().objectMapper();
  }

  @Test
  @DisplayName(
      "Given JSON with ad hoc availability month as integer, when deserializing, then should parse Month correctly")
  void shouldDeserializeAdHocAvailabilityMonthFromInteger() throws Exception {
    String json =
        """
            {
              "fullName": "John Doe",
              "position": "Engineer",
              "email": "john@example.com",
              "slackDisplayName": "john-slack",
              "country": {"countryCode": "US", "countryName": "USA"},
              "city": "New York",
              "companyName": "Tech Corp",
              "images": [],
              "network": [],
              "profileStatus": "ACTIVE",
              "bio": "Test bio",
              "skills": {
                "yearsExperience": 5,
                "areas": [],
                "languages": [],
                "mentorshipFocus": []
              },
              "spokenLanguages": [],
              "menteeSection": {
                "idealMentee": "I am seeking mentees who are enthusiastic about learning and growing in the field of technology. Whether you are a beginner looking to enhance your programming skills or an experienced professional aiming to delve deeper into cloud architecture and e-commerce systems, I am here to support your journey. I value proactive communication, eagerness to learn, and a collaborative spirit. If you are committed to expanding your knowledge and skills in software development, AWS architecture, CI/CD pipelines, and Terraform, I would be delighted to work with you.",
                "additional": "Career Growth and Development, Resume Review, Preparation for Technical Interviews, Leadership and Team Management Skills, Learning Resources and Skill Development Plans.",
                "longTerm": {
                  "numMentee": 2,
                  "hours": 4
                },
                "adHoc": [
                  { "month": 1, "hours": 3 },
                  { "month": 12, "hours": 3 }
                ]
              }
            }
            """;

    Mentor mentor = objectMapper.readValue(json, Mentor.class);

    assertEquals(Month.JANUARY, mentor.getMenteeSection().adHoc().get(0).month());
    assertEquals(Month.DECEMBER, mentor.getMenteeSection().adHoc().get(1).month());
  }

  @Test
  @DisplayName("Given JSON with invalid month integer, when deserializing, then should throw")
  void shouldThrowOnInvalidMonth() {
    String json =
        """
            { "month": 13, "hours": 3 }
            """;

    assertThrows(
        JsonMappingException.class,
        () -> objectMapper.readValue(json, MentorMonthAvailability.class));
  }
}
