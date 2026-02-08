package com.wcc.platform.deserializers;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcc.platform.configuration.ObjectMapperConfig;
import com.wcc.platform.domain.platform.mentorship.MenteeRegistration;
import java.time.Year;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for MenteeRegistration deserialization with custom YearDeserializer.
 */
class MenteeRegistrationDeserializationTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapperConfig().objectMapper();
  }

  @Test
  @DisplayName("Given JSON with cycleYear as integer, when deserializing, then should parse Year correctly")
  void shouldDeserializeCycleYearFromInteger() throws Exception {
    String json = """
        {
          "mentee": {
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
            "spokenLanguages": []
          },
          "mentorshipType": "LONG_TERM",
          "cycleYear": 2026,
          "applications": [
            {
              "menteeId": null,
              "mentorId": 1,
              "priorityOrder": 1
            }
          ]
        }
        """;

    MenteeRegistration registration = objectMapper.readValue(json, MenteeRegistration.class);

    assertThat(registration).isNotNull();
    assertThat(registration.cycleYear()).isEqualTo(Year.of(2026));
    assertThat(registration.mentee()).isNotNull();
    assertThat(registration.mentee().getFullName()).isEqualTo("John Doe");
  }

  @Test
  @DisplayName("Given JSON with cycleYear as string, when deserializing, then should parse Year correctly")
  void shouldDeserializeCycleYearFromString() throws Exception {
    String json = """
        {
          "mentee": {
            "fullName": "Jane Doe",
            "position": "Manager",
            "email": "jane@example.com",
            "slackDisplayName": "jane-slack",
            "country": {"countryCode": "US", "countryName": "USA"},
            "city": "Los Angeles",
            "companyName": "Innovation Inc",
            "images": [],
            "network": [],
            "profileStatus": "ACTIVE",
            "bio": "Test bio",
            "skills": {
              "yearsExperience": 10,
              "areas": [],
              "languages": [],
              "mentorshipFocus": []
            },
            "spokenLanguages": []
          },
          "mentorshipType": "AD_HOC",
          "cycleYear": "2026",
          "applications": [
            {
              "menteeId": null,
              "mentorId": 2,
              "priorityOrder": 1
            }
          ]
        }
        """;

    MenteeRegistration registration = objectMapper.readValue(json, MenteeRegistration.class);

    assertThat(registration).isNotNull();
    assertThat(registration.cycleYear()).isEqualTo(Year.of(2026));
    assertThat(registration.mentee().getFullName()).isEqualTo("Jane Doe");
  }
}
