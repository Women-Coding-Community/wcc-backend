package com.wcc.platform.domain.platform.member;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.PronounCategory;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.type.MemberType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Member class with common attributes for all community members. */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Builder(toBuilder = true)
public class Member {
  @Setter private Long id;
  @NotBlank private String fullName;
  @NotBlank private String position;
  @Setter @NotBlank @Email private String email;
  @NotBlank private String slackDisplayName;
  @NotNull private Country country;
  private String city;
  private String companyName;
  @Setter @NotNull private List<MemberType> memberTypes;
  private List<Image> images;
  private List<SocialNetwork> network;
  private String pronouns;
  private PronounCategory pronounCategory;

  public MemberDto toDto() {
    return new MemberDto(
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
        pronounCategory);
  }
}
