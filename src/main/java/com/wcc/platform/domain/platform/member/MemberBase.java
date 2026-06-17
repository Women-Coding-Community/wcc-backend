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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

/** Base class for Member and MemberDto to reduce code duplication. */
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@Validated
public abstract class MemberBase {
  @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Auto-generated member ID")
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @NotBlank(message = "Full name cannot be blank")
  private String fullName;

  @NotBlank(message = "Position cannot be blank")
  private String position;

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email format is not valid")
  @Setter
  private String email;

  @NotBlank(message = "Slack name cannot be blank")
  private String slackDisplayName;

  @NotNull(message = "Country cannot be null")
  private Country country;

  private String city;
  private String companyName;

  private List<MemberType> memberTypes;

  private List<Image> images;
  private List<SocialNetwork> network;
  private String pronouns;
  private PronounCategory pronounCategory;

  private Boolean isWomen;

  /** Converts this Member entity to a MemberDto for data transfer purposes. */
  public abstract MemberDto toDto();
}
