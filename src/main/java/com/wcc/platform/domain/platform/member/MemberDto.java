package com.wcc.platform.domain.platform.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import com.wcc.platform.domain.cms.attributes.PronounCategory;
import com.wcc.platform.domain.platform.SocialNetwork;
import com.wcc.platform.domain.platform.type.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.validation.annotation.Validated;

/** MemberDto class with common attributes for all community members. */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@Validated
public class MemberDto {
  @Schema(accessMode = Schema.AccessMode.READ_ONLY)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @NotBlank(message = "Full name cannot be blank")
  private String fullName;

  @NotBlank(message = "Position cannot be blank")
  private String position;

  @Setter
  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email format is not valid")
  private String email;

  @NotBlank(message = "Slack name cannot be blank")
  private String slackDisplayName;

  @NotNull(message = "Country cannot be null")
  private Country country;

  private String city;
  private String companyName;

  @Schema(
      accessMode = Schema.AccessMode.READ_ONLY,
      description = "List of Member types (e.g., Mentor, Leader, Volunteer, etc.)")
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private List<MemberType> memberTypes;

  private List<Image> images;
  private List<SocialNetwork> network;
  private String pronouns;
  private PronounCategory pronounCategory;
  private Boolean isWomen;

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
