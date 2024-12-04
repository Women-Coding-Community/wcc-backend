package com.wcc.platform.domain.cms.attributes;

import com.wcc.platform.domain.platform.SocialNetwork;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** Record for Contact CMS data. */
public record Contact(
    @NotBlank String title, String description, @NotEmpty List<SocialNetwork> links) {}
