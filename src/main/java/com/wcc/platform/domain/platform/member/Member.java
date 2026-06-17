package com.wcc.platform.domain.platform.member;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.PronounCategory;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.type.MemberType;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

/** Member class with common attributes for all community members. */
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@SuperBuilder(toBuilder = true)
@Validated
public class Member extends MemberBase {

  /** Constructor for SuperBuilder and manual use. */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public Member(
      final Long id,
      final String fullName,
      final String position,
      final String email,
      final String slackDisplayName,
      final Country country,
      final String city,
      final String companyName,
      final List<MemberType> memberTypes,
      final List<Image> images,
      final List<SocialNetwork> network,
      final String pronouns,
      final PronounCategory pronounCategory,
      final Boolean isWomen) {
    super(
        id,
        fullName,
        position,
        email,
        slackDisplayName,
        country,
        city,
        companyName,
        memberTypes,
        images,
        network,
        pronouns,
        pronounCategory,
        isWomen);
  }

  @Override
  @NotEmpty(message = "At least one member type must be provided (e.g., Member, Volunteer, etc.)")
  public List<MemberType> getMemberTypes() {
    return super.getMemberTypes();
  }

  /** Converts this Member entity to a MemberDto for data transfer purposes. */
  @Override
  public MemberDto toDto() {
    return new MemberDto(
        getId(),
        getFullName(),
        getPosition(),
        getEmail(),
        getSlackDisplayName(),
        getCountry(),
        getCity(),
        getCompanyName(),
        getMemberTypes(),
        getImages(),
        getNetwork(),
        getPronouns(),
        getPronounCategory(),
        getIsWomen());
  }
}
