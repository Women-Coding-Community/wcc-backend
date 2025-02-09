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

/** AboutHer class with common attributes for Celebrate her page. */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Data
@Builder(toBuilder = true)
public class AboutHer {
    @NotBlank
    private String hashtag;
    @NotBlank
    private List<String> listOfName;
    @NotBlank private String description;
    private SocialNetwork postLink;
}
