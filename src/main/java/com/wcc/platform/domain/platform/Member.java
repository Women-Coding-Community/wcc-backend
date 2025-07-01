package com.wcc.platform.domain.platform;

import com.wcc.platform.domain.cms.attributes.Country;
import com.wcc.platform.domain.cms.attributes.Image;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Member class with common attributes for all community members. */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Data
@Builder(toBuilder = true)
public class Member {
  private Long id;
  @NotBlank private String fullName;
  @NotBlank private String position;
  @NotBlank @Email private String email;
  @NotBlank private String slackDisplayName;
  @NotBlank private Country country;
  private String city;
  private String companyName;
  @NotNull private List<MemberType> memberTypes;
  @NotEmpty private List<Image> images;
  private List<SocialNetwork> network;
}
