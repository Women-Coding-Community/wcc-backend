package com.wcc.platform.domain.platform.mentorship;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class MentorDtoTest {

  @Test
  void shouldReturnTrueForEqualMentorDtos() {
    MentorDto mentor1 = MentorDto.mentorDtoBuilder().id(1L).build();
    MentorDto mentor2 = MentorDto.mentorDtoBuilder().id(1L).build();

    assertEquals(mentor1, mentor2);
  }

  @Test
  void shouldReturnFalseForDifferentMentorDtos() {
    MentorDto mentor1 = MentorDto.mentorDtoBuilder().id(1L).build();
    MentorDto mentor2 = MentorDto.mentorDtoBuilder().id(2L).build();

    assertNotEquals(mentor1, mentor2);
  }

  @Test
  void shouldReturnStringOfMentor() {
    MentorDto mentor =
        MentorDto.mentorDtoBuilder()
            .bio("bio info")
            .availability(new MentorAvailability(MentorshipType.AD_HOC, true))
            .spokenLanguages(List.of("English", "Spanish"))
            .fullName("Jane Doe")
            .id(1L)
            .build();
    var expected =
        "MentorDto(profileStatus=null, availability=MentorAvailability[mentorshipType=Ad-Hoc, available=true]"
            + ", skills=null, spokenLanguages=[English, Spanish], bio=bio info, menteeSection=null,"
            + " feedbackSection=null, resources=null)";

    assertEquals(expected, mentor.toString());
  }
}
