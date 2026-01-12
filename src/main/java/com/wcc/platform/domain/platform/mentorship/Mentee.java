package com.wcc.platform.domain.platform.mentorship;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.member.Member;
import com.wcc.platform.domain.platform.member.ProfileStatus;
import com.wcc.platform.domain.platform.type.MemberType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@SuppressWarnings({
    "PMD.LongVariable",
    "PMD.ExcessiveParameterList"
})
public class Mentee extends Member {

    private final @NotBlank MentorshipType previousMentorshipType;
    private final @NotBlank MentorshipType mentorshipType;
    private final @NotNull ProfileStatus profileStatus;
    private final @NotBlank Skills skills;
    private final @NotBlank String bio;
    private final List<String> spokenLanguages;

    @Builder(builderMethodName = "menteeBuilder")
    public Mentee(
        final Long id,
        @NotBlank final String fullName,
        @NotBlank final String position,
        @NotBlank @Email final String email,
        final String slackDisplayName,
        @NotBlank final Country country,
        @NotBlank final String city,
        final String companyName,
        @NotEmpty final List<Image> images,
        final List<SocialNetwork> network,
        @NotNull final ProfileStatus profileStatus,
        final List<String> spokenLanguages,
        @NotBlank final String bio,
        @NotBlank final Skills skills,
        @NotBlank final MentorshipType mentorshipType,
        @NotBlank final MentorshipType previousMentorshipType
    ) {
        super(
            id,
            fullName,
            position,
            email,
            slackDisplayName,
            country,
            city,
            companyName,
            Collections.singletonList(MemberType.MENTEE),
            images,
            network);

        this.profileStatus = profileStatus;
        this.skills = skills;
        this.spokenLanguages = spokenLanguages.stream().map(StringUtils::capitalize).toList();
        this.bio = bio;
        this.mentorshipType = mentorshipType;
        this.previousMentorshipType = previousMentorshipType;
    }
}
