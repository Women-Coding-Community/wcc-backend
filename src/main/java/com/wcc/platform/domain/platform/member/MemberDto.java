package com.wcc.platform.domain.platform.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.PronounCategory;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.type.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

/** MemberDto class with common attributes for all community members. */
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@Validated
public class MemberDto extends MemberBase {

  /** Constructor for SuperBuilder and manual use. */
  @SuppressWarnings("PMD.ExcessiveParameterList")
  public MemberDto(
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

  @Schema(
      accessMode = Schema.AccessMode.READ_ONLY,
      description = "List of Member types (e.g., Mentor, Leader, Volunteer, etc.)")
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Override
  public List<MemberType> getMemberTypes() {
    return super.getMemberTypes();
  }

  /** Converts this MemberDto entity to a MemberDto for data transfer purposes. */
  @Override
  public MemberDto toDto() {
    return this;
  }

  /**
   * Update member using attributes from his DTO.
   *
   * @param member member to be updated
   * @return Updated member
   */
  public Member merge(final Member member) {
    return member.toBuilder()
        .id(member.getId())
        .fullName(getFullName())
        .position(getPosition())
        .email(getEmail())
        .slackDisplayName(getSlackDisplayName())
        .country(getCountry())
        .city(getCity())
        .companyName(getCompanyName())
        .memberTypes(getMemberTypes())
        .images(getImages())
        .network(getNetwork())
        .pronouns(getPronouns())
        .pronounCategory(getPronounCategory())
        .isWomen(getIsWomen())
        .build();
  }
}
