package com.wcc.platform.factories;

import static com.wcc.platform.factories.SetupFactories.createMemberTest;

import com.wcc.platform.domain.cms.attributes.Languages;
import com.wcc.platform.domain.cms.attributes.MentorshipFocusArea;
import com.wcc.platform.domain.cms.attributes.ProficiencyLevel;
import com.wcc.platform.domain.cms.attributes.TechnicalArea;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.mentorship.LanguageProficiency;
import com.wcc.platform.domain.platform.mentorship.Mentee;
import com.wcc.platform.domain.platform.mentorship.Skills;
import com.wcc.platform.domain.platform.mentorship.TechnicalAreaProficiency;
import com.wcc.platform.domain.platform.type.MemberType;
import java.util.List;

/** Mentee test factories. */
public class SetupMenteeFactories {
  /** Mentee Builder. */
  public static Mentee createMenteeTest() {
    final Member member = createMemberTest(MemberType.MENTEE);
    return createMenteeTest(1L, member.getFullName(), member.getEmail());
  }

  /** Test factory for a Mentee. */
  public static Mentee createMenteeTest(
      final Long menteeId, final String name, final String email) {
    final Member member = createMemberTest(MemberType.MENTEE);

    Mentee.MenteeBuilder menteeBuilder =
        Mentee.menteeBuilder()
            .fullName(name)
            .position(member.getPosition())
            .email(email)
            .companyName(member.getCompanyName())
            .slackDisplayName(member.getSlackDisplayName())
            .country(member.getCountry())
            .city(member.getCity())
            .images(member.getImages())
            .pronouns(null)
            .pronounCategory(null)
            .profileStatus(ProfileStatus.ACTIVE)
            .bio("Mentee bio")
            .spokenLanguages(List.of("English"))
            .skills(
                new Skills(
                    2,
                    List.of(new TechnicalAreaProficiency(TechnicalArea.BACKEND, ProficiencyLevel.BEGINNER), new TechnicalAreaProficiency(TechnicalArea.FRONTEND, ProficiencyLevel.BEGINNER)),
                    List.of(new LanguageProficiency(Languages.JAVASCRIPT, ProficiencyLevel.BEGINNER)),
                    List.of(MentorshipFocusArea.GROW_BEGINNER_TO_MID)));
    if (menteeId != null) {
      menteeBuilder.id(menteeId);
    }

    return menteeBuilder.build();
  }
}
